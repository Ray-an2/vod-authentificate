package com.spring.authentificate.services.impl;

import com.spring.authentificate.clients.UserApiClient;
import com.spring.authentificate.dtos.UserAccountDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class UserApiClientImpl implements UserApiClient {

    private final RestClient restClient;
    private final String usersByPseudoPath;

    public UserApiClientImpl(
            RestClient.Builder restClientBuilder,
            @Value("${user.api.base-url}") String baseUrl,
            @Value("${user.api.users-by-pseudo-path:/users/pseudo/{pseudo}}") String usersByPseudoPath
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.usersByPseudoPath = usersByPseudoPath;
    }

    @Override
    public Optional<UserAccountDto> findByPseudo(String pseudo) {
        try {
            UserAccountDto account = restClient.get()
                    .uri(usersByPseudoPath, pseudo)
                    .retrieve()
                    .body(UserAccountDto.class);
            return Optional.ofNullable(account);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw exception;
        }
    }
}
