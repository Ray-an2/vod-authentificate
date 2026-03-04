package com.spring.authentificate.mappers;

import com.spring.authentificate.dtos.AuthResponseDto;
import com.spring.authentificate.entities.AuthToken;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuthMapper {

    public AuthResponseDto toAuthResponse(AuthToken authToken) {
        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setPseudo(authToken.getPseudo());
        responseDto.setRole(authToken.getRole());
        responseDto.setToken(authToken.getToken());

        long expiresIn = Math.max(0L, authToken.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond());
        responseDto.setExpiresIn(expiresIn);

        return responseDto;
    }
}
