package com.delivery.domain.auth.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.delivery.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthControllerUnitTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;

//    @Test
//    @DisplayName()
//    void signUp() {}
}
