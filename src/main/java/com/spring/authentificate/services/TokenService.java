package com.spring.authentificate.services;

public interface TokenService {

    String generateToken(String pseudo, String role);

    void validateToken(String token);

    long getExpirationSeconds();
}
