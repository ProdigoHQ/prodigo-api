package com.licode.prodigoerp.auth.application.service.platform;

import com.licode.prodigoerp.auth.application.port.input.PlatformAdminQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.RegisterPlatformAdminUseCase;
import com.licode.prodigoerp.auth.application.port.input.internal.AccessProvisioningUseCase;
import com.licode.prodigoerp.auth.application.port.input.internal.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.*;
import com.licode.prodigoerp.auth.application.port.input.query.UserSummaryQuery;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatformAdminService implements RegisterPlatformAdminUseCase, PlatformAdminQueryUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserUseCase saveUserUseCase;
    private final AccessProvisioningUseCase provisioning;
    private final CurrentUserPort currentUser;

    @Override
    @Transactional
    public String register(RegisterPlatformAdminCommand cmd) {

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
        provisioning.assignRoleToUser(fetchedUser.getId(), cmd.roleId(), null, author);

        log.info("Super admin {} created by {}", fetchedUser.getUsername(), author);
        return "Super admin " + fetchedUser.getUsername() + " created successfully";
    }

    @Override
    public List<UserSummaryQuery> get() {

        return loadUserPort.findAllUsersByIsSuperAdmin(true).stream().map(
                user -> {
                    return new UserSummaryQuery(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getStatus(),
                            user.getLastLogin(),
                            user.getCreatedAt()
                    );
                }
        ).toList();
    }
}
