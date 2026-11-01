package com.licode.prodigoerp.auth.application.port.input.result;

public record RefreshTokenResult(
        String accessToken,
        String refreshToken
) {
}
