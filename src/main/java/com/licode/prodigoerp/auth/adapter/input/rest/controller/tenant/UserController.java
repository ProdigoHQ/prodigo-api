package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.request.RegisterUserRequest;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.AuthResponse;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthRestMapper;
import com.licode.prodigoerp.auth.application.port.input.RegisterUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.result.AuthResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping(path = "/api/{version}/auth")
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthRestMapper authRestMapper;

    @PostMapping(value = "/register", version = "1.0")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest request) {

        AuthResult authResult = registerUserUseCase.register(authRestMapper.toRegisterUserCommand(request));

        String refreshToken = authResult.refreshToken();

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofDays(14))
                .sameSite("strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(
               authRestMapper.toAuthResponse(authResult)
        );
    }
}
