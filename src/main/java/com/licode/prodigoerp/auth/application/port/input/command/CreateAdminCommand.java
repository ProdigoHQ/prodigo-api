package com.licode.prodigoerp.auth.application.port.input.command;

import java.util.UUID;

public record CreateAdminCommand(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        UUID roleId
) {
}
