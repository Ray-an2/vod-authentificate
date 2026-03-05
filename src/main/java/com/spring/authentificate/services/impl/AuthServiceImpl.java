package com.spring.authentificate.services.impl;

import com.spring.authentificate.clients.UserApiClient;
import com.spring.authentificate.dtos.AuthResponseDto;
import com.spring.authentificate.dtos.LoginRequestDto;
import com.spring.authentificate.dtos.UserAccountDto;
import com.spring.authentificate.entities.AuthToken;
import com.spring.authentificate.mappers.AuthMapper;
import com.spring.authentificate.repositories.AuthTokenRepository;
import com.spring.authentificate.services.AuthService;
import com.spring.authentificate.services.TokenService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserApiClient userApiClient;
    private final TokenService tokenService;
    private final AuthTokenRepository authTokenRepository;
    private final AuthMapper authMapper;

    public AuthServiceImpl(
            UserApiClient userApiClient,
            TokenService tokenService,
            AuthTokenRepository authTokenRepository,
            AuthMapper authMapper
    ) {
        this.userApiClient = userApiClient;
        this.tokenService = tokenService;
        this.authTokenRepository = authTokenRepository;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        UserAccountDto user = userApiClient.findByLogin(loginRequestDto.getPseudo(), loginRequestDto.getMdp())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouve"));

        String token = tokenService.generateToken(user.getPseudo(), user.getRole());

        AuthToken authToken = new AuthToken();
        authToken.setPseudo(user.getPseudo());
        authToken.setRole(user.getRole());
        authToken.setToken(token);
        authToken.setExpiresAt(Instant.now().plusSeconds(tokenService.getExpirationSeconds()));

        AuthToken savedToken = authTokenRepository.save(authToken);
        return authMapper.toAuthResponse(savedToken);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyToken(String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        try {
            tokenService.validateToken(token);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide ou expire", exception);
        }

        AuthToken authToken = authTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inconnu"));

        if (authToken.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide ou expire");
        }
    }

    @Override
    public void logout(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (!authTokenRepository.existsById(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inconnu");
        }
        authTokenRepository.deleteById(token);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header Authorization invalide");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token manquant");
        }

        return token;
    }
}
