package com.licode.prodigoerp.auth.application.port.input;


import com.licode.prodigoerp.auth.application.port.input.command.CreateAdminCommand;

public interface RegisterPlatformAdminUseCase {

    String register(CreateAdminCommand command);
}
