package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.RefreshTokenUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RefreshResponseCommand;
import com.licode.prodigoerp.auth.application.port.output.RefreshTokenStorePort;
import com.licode.prodigoerp.auth.application.port.output.TokenGeneratorPort;
import com.licode.prodigoerp.auth.domain.model.RefreshToken;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements RefreshTokenUseCase {

    private final RefreshTokenStorePort refreshTokenStorePort;
    private final TokenGeneratorPort tokenGeneratorPort;

    // NOTE: for the refresh token flow
    // The refresh token string (from the request)
    // With this string, we can find the RefreshToken object that will also contain the user infos
    // If a wrong refresh token string is send by the request, we will not be able to find the user
    // leading to no generation of the new access token
    @Override
    @Transactional
    public RefreshResponseCommand refreshToken(String refreshToken) {

        Optional<RefreshToken> fetchedRefreshTokenObj = refreshTokenStorePort.findRefreshTokenByTokenString(refreshToken);

        if (fetchedRefreshTokenObj.isEmpty()) {
            log.error("No refresh token found with the string provided");
            throw new NotFoundException("Refresh token not found");
        }

        RefreshToken refreshTokenObj = fetchedRefreshTokenObj.get();

        // need to do verification to be sure the Refresh token is a valid Token
        if(refreshTokenObj.getIsRevoked()){
            log.error("The refresh token has been revoked");
            throw new AccessDeniedException("Access Denied! Refresh token has been revoked");
      }

      if(refreshTokenObj.getExpiryDate().isBefore(Instant.now())){
          log.error("The refresh token has expired");
          throw new BadCredentialsException("Bad credentials! Your token has expired");
      }

//     NOTE: before issuing any new refresh token, revoke the old ones
      refreshTokenStorePort.revokeRefreshTokenByTokenString(refreshTokenObj.getToken());

      User user = refreshTokenObj.getUser();


      RefreshToken newRefreshToken = refreshTokenStorePort.createRefreshToken(user);
      String newAccessToken = tokenGeneratorPort.generateAccessToken(user);

      log.info("New access token generated for the user with id: {}", user.getId());
      return new RefreshResponseCommand(
              newAccessToken,
              newRefreshToken.getToken()
      );
    }

    @Override
    public void logout(String token) {
        refreshTokenStorePort.revokeRefreshTokenByTokenString(token);
    }

}
