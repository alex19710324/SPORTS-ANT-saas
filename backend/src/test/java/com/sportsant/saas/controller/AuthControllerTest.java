package com.sportsant.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.dto.LoginRequest;
import com.sportsant.saas.dto.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignupAndLogin() throws Exception {
        String uniqueUsername = "user" + System.currentTimeMillis() % 10000; // Keep username short (<20 chars)
        String uniqueEmail = uniqueUsername + "@example.com";
        String password = "password123";

        // 1. Signup
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(uniqueUsername);
        signupRequest.setEmail(uniqueEmail);
        signupRequest.setPassword(password);
        signupRequest.setRole(Set.of("user"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // 2. Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(uniqueUsername);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(uniqueUsername));
    }

    @Test
    public void testLoginFailure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("non_existent_user");
        loginRequest.setPassword("wrong_password");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
