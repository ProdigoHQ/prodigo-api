package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.AccessQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.RegisterUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.input.internal.AccessProvisioningUseCase;
import com.licode.prodigoerp.auth.application.port.input.internal.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.output.*;
import com.licode.prodigoerp.auth.domain.model.RefreshToken;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import com.licode.prodigoerp.module.application.port.input.TenantModuleSubCreateUseCase;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.CreateTenantUseCase;
import com.licode.prodigoerp.tenant.application.port.input.TenantEntitlementUseCase;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.application.port.input.command.CreateTenantCommand;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnboardingService implements RegisterUserUseCase {

    private final LoadUserPort loadUserPort;
    private final TenantLookUpUseCase tenantLookUp;
    private final CreateTenantUseCase createTenantUseCase;
    private final TenantEntitlementUseCase  tenantEntitlementUseCase;
    private final TenantModuleSubCreateUseCase tenantModuleSubCreateUseCase;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final TokenGeneratorPort  tokenGeneratorPort;
    private final SaveUserUseCase saveUserUseCase;
    private final AccessProvisioningUseCase provisioning;
    private final AccessQueryUseCase accessQuery;
    private final CurrentUserPort currentUser;



    @Override
    @Transactional
    public AuthResponseCommand register(RegisterUserCommand cmd) {

        if(loadUserPort.findUserByUsername(cmd.username()).isPresent()){
            log.error("Username: {} already exists",  cmd.username());
            throw new ConflictException("Username already exists, Please try another one.");
        }

        if(loadUserPort.findUserByEmail(cmd.email()).isPresent() ) {
            log.error("Email: {} already exists",  cmd.email());
            throw new ConflictException("Email already exists,  Please try another email.");
        };

        if(tenantLookUp.existsBySlug(cmd.companySlug())){
            log.error("Company name (slug): {} already exists",  cmd.companySlug());
            throw new ConflictException("Company Name already exists, Please try another one.");
        }

        String author = currentUser.usernameOrSystem();

        // creating a tenant while registering the user
        // IMPORTANT: here the principle is that user creating his account is the owner of the company
        // there will be endpoints for the users registration on a specific tenant
        Tenant createdTenant = createTenantUseCase.create(
                new CreateTenantCommand(
                        cmd.companyName(),
                        cmd.companySlug(),
                        cmd.country()
                )
        );

        tenantEntitlementUseCase.createDefaultTenantEntitlement(createdTenant);

        // Then we need to create the moduleSub from the selected module provided
        // NOTE: the first module in the list of the selected module is free
        Map<String, Module> subscriptions = tenantModuleSubCreateUseCase.createTenantModuleSubscription(
                createdTenant.getId(),
                cmd.selectedModules()
        );

        User fetchedUser = saveUserUseCase.save(
                new CreateUserCommand(
                        cmd.username(),
                        createdTenant,
                        cmd.email(),
                        cmd.password(),
                        cmd.firstName(),
                        cmd.lastName(),
                        false
                ),
                author
        );


        // here is the Admin role ( for the company (tenant) creating the account)
        // TODO: Also need an OWNER role along side with the ADMIN role (TO BE IMPLEMENTED)
        UUID adminRoleId = provisioning.createTenantAdminRole(createdTenant.getId(), subscriptions.keySet(), author);
        provisioning.assignRoleToUser(fetchedUser.getId(), adminRoleId, createdTenant.getId(), author);

        // Generating the access and refresh token
        RefreshToken refreshToken = refreshTokenStorePort.createRefreshToken(fetchedUser);
        String accessToken = tokenGeneratorPort.generateAccessToken(fetchedUser);

        UserAccessCommand access = accessQuery.activeAccess(fetchedUser.getId());

        log.info("Tenant {} registered with admin user {}", createdTenant.getSlug(), fetchedUser.getUsername());

        return new AuthResponseCommand(
                fetchedUser.getId(),
                createdTenant.getSlug(),
                accessToken,
                refreshToken.getToken(),
                access.roles(),
                access.permissions()
        );
    }
}
