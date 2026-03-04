package com.spring.authentificate.controllers;

import com.spring.authentificate.dtos.AuthResponseDto;
import com.spring.authentificate.dtos.LoginRequestDto;
import com.spring.authentificate.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentificate")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @GetMapping("/verify-token")
    public ResponseEntity<Void> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        authService.verifyToken(authorizationHeader);
        return ResponseEntity.ok().build();
    }
}
