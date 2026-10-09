package com.licode.prodigoerp.auth.adapter.input.rest.controller.tenant;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.AuthResponseDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.RegisterRequestDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthWebMapper;
import com.licode.prodigoerp.auth.application.port.input.RegisterUserUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.AuthResponseCommand;
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
    private final AuthWebMapper authWebMapper;

    @PostMapping(value = "/register", version = "1.0")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {

        AuthResponseCommand authResponseCommand = registerUserUseCase.register(authWebMapper.toRegisterUserCommand(registerRequestDto));

        String refreshToken = authResponseCommand.refreshToken();

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofDays(14))
                .sameSite("strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(
               authWebMapper.toAuthResponseDto(authResponseCommand)
        );
    }
}
