package com.licode.prodigoerp.auth.application.port.input.command;

public record EnsurePermissionCommand(String moduleKey, String resource, String action, String description) {
}
