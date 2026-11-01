package com.licode.prodigoerp.auth.application.port.input.command;

public record LoginCommand(
        String username,
        String password
) {
}
