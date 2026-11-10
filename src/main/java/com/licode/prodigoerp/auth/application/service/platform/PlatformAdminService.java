package com.licode.prodigoerp.auth.application.service.platform;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.UpdateUserStatusRequest;
import com.licode.prodigoerp.auth.application.helpers.AuthHelpers;
import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.input.PlatformAdminQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.RegisterPlatformAdminUseCase;
import com.licode.prodigoerp.auth.application.port.input.internal.AccessProvisioningUseCase;
import com.licode.prodigoerp.auth.application.port.input.internal.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.auth.application.port.input.result.RoleSummaryResult;
import com.licode.prodigoerp.auth.application.port.input.result.UserDetailsResult;
import com.licode.prodigoerp.auth.application.port.input.result.UserSummaryResult;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.AccountStatus;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.ForbiddenException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatformAdminService implements RegisterPlatformAdminUseCase, PlatformAdminQueryUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserUseCase saveUserUseCase;
    private final AccessProvisioningUseCase provisioning;
    private final CurrentUserPort currentUser;
    private final RolePersistencePort rolePort;

    @Override
    @Transactional
    public String register(CreateAdminCommand cmd) {

        if(loadUserPort.findUserByUsername(cmd.username()).isPresent()){
            log.error("Username {} already exists", cmd.username());
            throw new ConflictException("Username already exists, please create another username");
        }

        if(loadUserPort.findUserByEmail(cmd.email()).isPresent() ) {
            log.error("Email {} already exists", cmd.email());
            throw new ConflictException("Email already exists,  please try another email");
        };

        String author = currentUser.usernameOrSystem();

        User fetchedUser = saveUserUseCase.save(
                new CreateUserCommand(
                        cmd.username(),
                        null,
                        cmd.email(),
                        cmd.password(),
                        cmd.firstName(),
                        cmd.lastName(),
                        true
                ),
                author
        );

//        UUID roleId = provisioning.ensureSuperAdminRole(author);
        provisioning.assignRoleToUser(new AssignRoleCommand(
                fetchedUser.getId(),
                cmd.roleId(),
                null,
                author
        ));

        log.info("New platform admin {} created by {}", fetchedUser.getUsername(), author);
        return "Platform admin " + fetchedUser.getUsername() + " created successfully";
    }

    @Override
    @Transactional
    public String update(UUID adminId, UpdatedUserCommand command) {
        User fetchedAdmin =  findUserDataById(adminId);
        String currentAuthor = currentUser.username();

        User updatedUser = saveUserUseCase.update(command, fetchedAdmin, currentAuthor);

        log.info("Platform admin {} updated by {}", updatedUser.getUsername(), currentAuthor);
        return "Platform admin with  id " + fetchedAdmin.getId() + " updated successfully";
    }

    @Override
    public String status(UUID adminId, UpdateUserStatusCommand status) {

        User fetchedAdmin = findUserDataById(adminId);
        String currentAuthor = currentUser.usernameOrSystem();

        // verify if the status passed is in AccountStatus
//        if (AuthHelpers.isValidAccountStatus(status.status().toString())) {
//            throw new ForbiddenException("Account status: " + status +" not allowed");
//        }

        AccountStatus newStatus = AccountStatus.valueOf(status.status().toString());

        User updatedUser = saveUserUseCase.status(newStatus, fetchedAdmin, currentAuthor);
        log.info("{}'s account status: {} was updated by {}", fetchedAdmin.getUsername(), newStatus, currentAuthor);

        return "Platform admin with  id " + fetchedAdmin.getId() + " Status updated successfully";
    }

    @Override
    public List<UserSummaryResult> findAllAdmins() {

        return loadUserPort.findAllUsersByIsSuperAdmin(true).stream()
                .map(AuthMappings::toUserSummary)
                .toList();
    }

    @Override
    public UserDetailsResult findUserById(UUID id) {
        User fetchedAdmin =  findUserDataById(id);

        // fetching for the role associated to the user
        Role fetchedRole = rolePort.findRoleByUserId(id).orElseThrow(() -> new NotFoundException("No Admin role assigned to the user with id: " + id));

        // fetch for the user's permissions 1:n
        List<PermissionSummaryResult> permissions = rolePort.findPermissionsByRoleId(fetchedRole.getId())
                .stream().map(AuthMappings::toSummary)
                .toList();

        List<RoleSummaryResult> roles = new ArrayList<>();
        roles.add(
                AuthMappings.toSummary(fetchedRole)
        );


        return new  UserDetailsResult(
                fetchedAdmin.getId(),
                fetchedAdmin.getUsername(),
                fetchedAdmin.getEmail(),
                fetchedAdmin.getFirstName(),
                fetchedAdmin.getLastName(),
                fetchedAdmin.getStatus(),
                fetchedAdmin.getLastLogin(),
                fetchedAdmin.getCreatedAt(),
                fetchedAdmin.getCreatedBy(),
                roles,
                permissions
        );
    }

    // helper function

    private User findUserDataById(UUID id){
        return loadUserPort.findUserById(id).orElseThrow(() -> new NotFoundException("No admin found with this id: " + id));
    }
}
