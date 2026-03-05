package com.spring.authentificate.repositories;

import com.spring.authentificate.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

    Optional<AuthToken> findByToken(String token);

    void deleteByExpiresAtBefore(Instant now);
}
