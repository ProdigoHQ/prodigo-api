package com.licode.prodigoerp.auth.adapter.input.rest.controller;


import com.licode.prodigoerp.auth.adapter.input.rest.dto.AuthResponseDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.LoginRequestDto;
import com.licode.prodigoerp.auth.adapter.input.rest.dto.RefreshResponseDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthWebMapper;
import com.licode.prodigoerp.auth.application.port.input.RefreshTokenUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RefreshResponseCommand;
import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.RefreshTokenStorePort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.application.port.output.TokenGeneratorPort;
import com.licode.prodigoerp.auth.domain.model.RefreshToken;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.config.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/{version}/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager  authenticationManager;
    private final LoadUserPort loadUserPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final TokenGeneratorPort tokenGeneratorPort;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final AuthWebMapper authWebMapper;
    private final RoleQueryPort roleQueryPort;

    @PostMapping(value = "/login", version = "1.0")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto){

        // TODO: Need to find a way to refactor the login to follow the Hexagonal Archi
        // Since most of the login logic is handle by spring security
        // I don't want to make these logics in service to avoid complete coupling with spring security

        Authentication authenticatedUser =  authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password()
                ));

        if( !authenticatedUser.isAuthenticated()){
            throw new BadCredentialsException("Bad credentials");
        }

        Optional<User> user = loadUserPort.findUserByUsername(loginRequestDto.username());

        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        RefreshToken refreshToken = refreshTokenStorePort.createRefreshToken(user.get());
        String accessToken = tokenGeneratorPort.generateAccessToken(user.get());

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofDays(14))
                .sameSite("strict")
                .build();

        String tenantSlug = user.get().getTenant() == null ? null : user.get().getTenant().getSlug();

        // TODO: Load the user roles and permissions here
        List<String> roles = roleQueryPort.findActiveRoleNames(user.get().getId());
        List<String> permissions = roleQueryPort.findActivePermissionCodes(user.get().getId());

        return  ResponseEntity.ok().header(
                HttpHeaders.SET_COOKIE, responseCookie.toString()
        ).body(
                new AuthResponseDto(
                     user.get().getId(),
                        tenantSlug,
                        accessToken,
                        roles,
                        permissions
                )
        );
    }

    @GetMapping(value = "/refresh", version = "1.0")
    public ResponseEntity<RefreshResponseDto> refresh(@CookieValue("refresh_token") String refreshToken) {

        RefreshResponseCommand refreshResponse = refreshTokenUseCase.refreshToken(refreshToken);

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshResponse.refreshToken())
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofDays(14))
                .sameSite("strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(
            authWebMapper.toRefreshResponseDto(refreshResponse)
        );
    }

    @GetMapping(value = "/logout", version = "1.0")
    public ResponseEntity<Void> logout(@CookieValue("refresh_token") String refreshToken) {

        refreshTokenUseCase.logout(refreshToken);

        // Clear the current Tenant in the TenantContext
        TenantContext.clearCurrentTenant();

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(0)
                .sameSite("strict")
                .build();


        SecurityContextHolder.getContext().setAuthentication(null);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();

    }
}
