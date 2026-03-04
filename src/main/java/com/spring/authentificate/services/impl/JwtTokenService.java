package com.spring.authentificate.services.impl;

import com.spring.authentificate.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService implements TokenService {

    private final byte[] signingKey;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${auth.token.secret}") String secret,
            @Value("${auth.token.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.signingKey = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generateToken(String pseudo, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .subject(pseudo)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))
                .build()
                .parseSignedClaims(token);
    }

    @Override
    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public Claims readClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
