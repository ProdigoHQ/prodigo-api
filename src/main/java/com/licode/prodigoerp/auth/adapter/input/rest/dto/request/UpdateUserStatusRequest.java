package com.licode.prodigoerp.auth.adapter.input.rest.dto.request;

import com.licode.prodigoerp.auth.domain.model.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull AccountStatus status
        ) {
}
