package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.RegisterUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.SaveAuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RefreshTokenStorePort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.TokenGeneratorPort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.RefreshToken;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.module.application.port.input.TenantModuleSubCreateUseCase;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.CreateTenantUseCase;
import com.licode.prodigoerp.tenant.application.port.input.TenantEntitlementUseCase;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.application.port.input.command.CreateTenantCommand;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase {

    private final LoadUserPort loadUserPort;
    private final TenantLookUpUseCase tenantLookUpUseCase;
    private final CreateTenantUseCase createTenantUseCase;
    private final TenantEntitlementUseCase  tenantEntitlementUseCase;
    private final TenantModuleSubCreateUseCase tenantModuleSubCreateUseCase;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final SaveUserUseCase saveUserUseCase;
    private final SaveAuthoritiesUseCase saveAuthoritiesUseCase;
    private final TokenGeneratorPort  tokenGeneratorPort;
    private final RoleQueryPort roleQueryPort;



    @Override
    @Transactional
    public AuthResponseCommand register(RegisterUserCommand registerUserCommand) {

        // check if the email, username already exist in the db
        if(loadUserPort.findUserByUsername(registerUserCommand.username()).isPresent()){
            throw new ConflictException("Username already exists");
        }

        if(loadUserPort.findUserByEmail(registerUserCommand.email()).isPresent() ) {
            throw new ConflictException("Email already exists");
        };

        // also need to check is the Company exist or not in the db
        if(tenantLookUpUseCase.existsBySlug(registerUserCommand.companySlug())){
            throw new ConflictException("Company Name already exists");
        }

        // creating a tenant while registering the user
        // IMPORTANT: here the principle is that user creating his account is the owner of the company
        // there will be endpoints for the users registration on a specific tenant
        Tenant createdTenant = createTenantUseCase.create(
                new CreateTenantCommand(
                        registerUserCommand.companyName(),
                        registerUserCommand.companySlug(),
                        registerUserCommand.country()
                )
        );

        // After creating the tenant, we need to create its tenant entitlements (with the default values)
        tenantEntitlementUseCase.createDefaultTenantEntitlement(createdTenant);

        // Then we need to create the moduleSub from the selected module provided
        // NOTE: the first module in the list of the selected module is free
        // handle the module subscription
        Map<String, Module> subscriptions = tenantModuleSubCreateUseCase.createTenantModuleSubscription(
                createdTenant.getId(),
                registerUserCommand.selectedModules()
        );

        // Save the user to the DB but first need to build the user
        String author = "PRODIGO_ERP_API"; // SINCE it is the system creating the user

        User fetchedUser = saveUserUseCase.save(
                new CreateUserCommand(
                        registerUserCommand.username(),
                        createdTenant,
                        registerUserCommand.email(),
                        registerUserCommand.password(),
                        registerUserCommand.firstName(),
                        registerUserCommand.lastName(),
                        false
                ),
                author
        );


        // here is the Admin role ( for the company (tenant) creating the account)
        // there will be duplicate ADMIN roles here but diff Tenant
        Role adminRole = saveAuthoritiesUseCase.saveRole(
                new CreateRoleCommand(
                        "ADMIN",
                        createdTenant,
                        "ROLE_ADMIN : This is the role that has full access to the Tenant (Company)",
                        false,
                        author
                )
        );

        // Then we need to associate the admin role to the user
        saveAuthoritiesUseCase.assignedRoleToUser(
                new AssignRoleCommand(
                        fetchedUser.getId(),
                        adminRole.getId(),
                        createdTenant.getId(),
                        author
                )
        );

        // we need to assign the full access permissions (depending on the to the user
        //NOTE: since this will be the endpoint for the Tenant Admin creation,
        // he/she will have all the permissions i.e. ModuleKey.Resource.CRUD (e.x: CRM.CRM.CRUD or INVOICE.INVOICE_CRUD)

        subscriptions.forEach((key, value) -> {

            // we need assign the full access permission of all the modules to the role
            // NOTE: we only get the full access permission code (key.MODULE.CRUD) nothing else
            String permissionString = key + "." + "MODULE" + "." + "CRUD";
            Optional<Permission> permission = roleQueryPort.findPermissionByCode(permissionString);

            if(permission.isEmpty()) {
                throw new NotFoundException("Permission with code " + permissionString + " not found");
            }


            saveAuthoritiesUseCase.assignedPermissionToRole(
                    permission.get().getId(),
                    new AssignRoleCommand(
                           fetchedUser.getId(),
                           adminRole.getId(),
                           createdTenant.getId(),
                           author
                    )
            );
        });

        // Generating the access and refresh token
        RefreshToken refreshToken = refreshTokenStorePort.createRefreshToken(fetchedUser);
        String accessToken = tokenGeneratorPort.generateAccessToken(fetchedUser);

        List<String> roles = roleQueryPort.findActiveRoleNames(fetchedUser.getId());
        List<String> permissions = roleQueryPort.findActivePermissionCodes(fetchedUser.getId());

        return new AuthResponseCommand(
                fetchedUser.getId(),
                createdTenant.getSlug(),
                accessToken,
                refreshToken.getToken(),
                roles,
                permissions
        );
    }
}
