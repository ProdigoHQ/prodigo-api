package com.licode.prodigoerp.auth.application.port.input.result;

import java.util.List;

public record UserAccessResult(List<String> roles, List<String> permissions) {
}
