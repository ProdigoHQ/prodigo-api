package com.licode.prodigoerp.auth.application.port.input;


import com.licode.prodigoerp.auth.application.port.input.command.CreateAdminCommand;
import com.licode.prodigoerp.auth.application.port.input.command.UpdatedUserCommand;

import java.util.UUID;

public interface RegisterPlatformAdminUseCase {

    String register(CreateAdminCommand command);
    String update(UUID adminId, UpdatedUserCommand command);
}
