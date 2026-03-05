package com.spring.authentificate.controllers;

import com.spring.authentificate.dtos.AuthResponseDto;
import com.spring.authentificate.dtos.LoginRequestDto;
import com.spring.authentificate.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(authService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login() throws Exception {
        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setPseudo("bosswasa");
        responseDto.setRole("U");
        responseDto.setToken("jwt-token-value");
        responseDto.setExpiresIn(10800);

        when(authService.login(any())).thenReturn(responseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readJson("json/auth-login-request.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("bosswasa"))
                .andExpect(jsonPath("$.role").value("U"))
                .andExpect(jsonPath("$.token").value("jwt-token-value"))
                .andExpect(jsonPath("$.expiresIn").value(10800));

        ArgumentCaptor<LoginRequestDto> loginCaptor = ArgumentCaptor.forClass(LoginRequestDto.class);
        verify(authService).login(loginCaptor.capture());

        LoginRequestDto captured = loginCaptor.getValue();
        assertThat(captured.getPseudo()).isEqualTo("bosswasa");
        assertThat(captured.getMdp()).isEqualTo("password");
    }

    @Test
    void verifyToken() throws Exception {
        String authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock.signature";
        doNothing().when(authService).verifyToken(authorization);

        mockMvc.perform(get("/auth/verify-token")
                        .header("Authorization", authorization))
                .andExpect(status().isOk());

        verify(authService).verifyToken(authorization);
    }

    @Test
    void logout() throws Exception {
        String authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock.signature";
        doNothing().when(authService).logout(authorization);

        mockMvc.perform(delete("/auth/logout")
                        .header("Authorization", authorization))
                .andExpect(status().isNoContent());

        verify(authService).logout(authorization);
    }

    private String readJson(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        try (var inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
