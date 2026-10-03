package com.licode.prodigoerp.auth.application.port.input.command;

public record SaveRoleCommand(
        String name,
        String description
) {
}
