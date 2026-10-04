package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.internal.SaveUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.CreateUserCommand;
import com.licode.prodigoerp.auth.application.port.output.PasswordEncoderPort;
import com.licode.prodigoerp.auth.application.port.output.SaveUserPort;
import com.licode.prodigoerp.auth.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SaveUserService implements SaveUserUseCase {
    private final PasswordEncoderPort passwordEncoderPort;
    private final SaveUserPort saveUserPort;

    @Override
    public User save(CreateUserCommand command, String author) {

        User createdUser = new User();
        Instant now = Instant.now();

        createdUser.setId(null);
        createdUser.setUsername(command.username());
        createdUser.setEmail(command.email());

        String hashPassword = passwordEncoderPort.encode(command.password());
        createdUser.setPassword(hashPassword);
        createdUser.setTenant(command.tenant());
        createdUser.setFirstName(command.firstName());
        createdUser.setLastName(command.lastName());
        createdUser.setStatus("ACTIVE");
        createdUser.setIsSuperAdmin(command.isSuperAdmin());

        createdUser.setLastLogin(now);
        createdUser.setCreatedAt(now);
        createdUser.setUpdatedAt(now);

        createdUser.setCreatedBy(author);
        createdUser.setUpdatedBy(author);

        return saveUserPort.save(createdUser);
    }
}
