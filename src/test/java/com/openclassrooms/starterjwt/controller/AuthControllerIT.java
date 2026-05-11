package com.openclassrooms.starterjwt.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.openclassrooms.starterjwt.dto.request.LoginRequest;
import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.entity.User;

@DisplayName("AuthController – Integration Tests")
class AuthControllerIT extends AbstractIntegrationTest {

    // ─── POST /api/auth/login ─────────────────────────────────────────────

    @Test
    @DisplayName("login – should return 200 and JWT when credentials are valid")
    void login_validCredentials_shouldReturn200WithJwt() throws Exception {

        // ✅ Création utilisateur réel en base (IMPORTANT)
        User user = User.builder()
                .email("login@test.com")
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("login@test.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("login@test.com"));
    }

    @Test
    @DisplayName("login – should return admin flag when user is admin")
    void login_adminUser_shouldReturnAdminFlag() throws Exception {

        User user = User.builder()
                .email("admin-login@test.com")
                .firstName("Admin")
                .lastName("User")
                .password(passwordEncoder.encode("password123"))
                .admin(true)
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin-login@test.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @DisplayName("login – should return 401 when password is wrong")
    void login_wrongPassword_shouldReturn401() throws Exception {

        User user = User.builder()
                .email("login2@test.com")
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode("correctPassword"))
                .admin(false)
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("login2@test.com");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login – should return 401 when user does not exist")
    void login_unknownUser_shouldReturn401() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@test.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login – should return 400 when email is blank")
    void login_blankEmail_shouldReturn400() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─── POST /api/auth/register ──────────────────────────────────────────

    @Test
    @DisplayName("register – should return 200 when registration is successful")
    void register_validRequest_shouldReturn200() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setEmail("newuser@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("register – should return 400 when email is already taken")
    void register_duplicateEmail_shouldReturn400() throws Exception {

        // ✅ utilisateur déjà existant
        User user = User.builder()
                .email("existing@test.com")
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();

        userRepository.save(user);

        SignupRequest request = new SignupRequest();
        request.setEmail("existing@test.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    @DisplayName("register – should return 400 when password is too short")
    void register_shortPassword_shouldReturn400() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setEmail("short@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register – should return 400 when email is missing")
    void register_missingEmail_shouldReturn400() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setEmail(null);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
