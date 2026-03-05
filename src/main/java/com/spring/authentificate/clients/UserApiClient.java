package com.spring.authentificate.clients;

import com.spring.authentificate.dtos.UserAccountDto;

import java.util.Optional;

public interface UserApiClient {
    Optional<UserAccountDto> findByLogin(String pseudo, String mdp);
}
