package com.licode.prodigoerp.auth.application.port.input;


import com.licode.prodigoerp.auth.application.port.input.command.RegisterPlatformAdminCommand;

public interface RegisterPlatformAdminUseCase {

    String register(RegisterPlatformAdminCommand registerSuperAdminCommand);
}
