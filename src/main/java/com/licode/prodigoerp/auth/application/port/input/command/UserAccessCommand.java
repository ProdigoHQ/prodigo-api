package com.licode.prodigoerp.auth.application.port.input.command;

import java.util.List;

public record UserAccessCommand(List<String> roles, List<String> permissions) {
}
