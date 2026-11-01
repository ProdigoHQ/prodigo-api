package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.RegisterUserCommand;
import com.licode.prodigoerp.auth.application.port.input.result.AuthResult;

public interface RegisterUserUseCase {

    AuthResult register(RegisterUserCommand registerUserCommand);

}
