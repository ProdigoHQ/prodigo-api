package com.licode.prodigoerp.common.exception.dto.response;

public record FieldErrorResponse(
        String field,
        String message
) {
}