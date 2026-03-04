package com.spring.authentificate.services;

import com.spring.authentificate.dtos.AuthResponseDto;
import com.spring.authentificate.dtos.LoginRequestDto;

public interface AuthService {

    AuthResponseDto login(LoginRequestDto loginRequestDto);

    void verifyToken(String authorizationHeader);
}
