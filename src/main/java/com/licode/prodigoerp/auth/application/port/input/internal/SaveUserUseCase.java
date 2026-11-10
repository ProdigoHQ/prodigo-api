package com.licode.prodigoerp.auth.application.port.input.internal;

import com.licode.prodigoerp.auth.application.port.input.command.CreateUserCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatedUserCommand;
import com.licode.prodigoerp.auth.domain.model.AccountStatus;
import com.licode.prodigoerp.auth.domain.model.User;

public interface SaveUserUseCase {

    User save(CreateUserCommand command, String author);
    User update(UpdatedUserCommand command, User user, String author);
    User status(AccountStatus newStatus, User user, String author);
}
