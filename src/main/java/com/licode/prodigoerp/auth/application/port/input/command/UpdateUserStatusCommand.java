package com.licode.prodigoerp.auth.application.port.input.command;

import com.licode.prodigoerp.auth.domain.model.AccountStatus;

public record UpdateUserStatusCommand(
        AccountStatus status
) {
}
