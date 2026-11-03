package com.licode.prodigoerp.auth.application.port.input.command;

public record UpdatedUserCommand(
        String firstName,
        String lastName
) {
}
