package com.licode.prodigoerp.common.exception.dto.response;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String errorCode,
        String traceId,
        List<FieldErrorResponse> errors
) {
}
