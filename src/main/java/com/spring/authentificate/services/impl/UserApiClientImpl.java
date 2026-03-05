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
    private final String loginCheckPath;

    public UserApiClientImpl(
            RestClient.Builder restClientBuilder,
            @Value("${user.api.base-url}") String baseUrl,
            @Value("${user.api.login-check-path:/users/login}") String loginCheckPath
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.loginCheckPath = loginCheckPath;
    }

    @Override
    public Optional<UserAccountDto> findByLogin(String pseudo, String mdp) {
        try {
            UserAccountDto account = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(loginCheckPath)
                            .queryParam("pseudo", pseudo)
                            .queryParam("mdp", mdp)
                            .build())
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
