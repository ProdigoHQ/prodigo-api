package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.result.RefreshTokenResult;

public interface RefreshTokenUseCase {
    RefreshTokenResult refreshToken(String refreshToken);
    void logout(String token);
}
