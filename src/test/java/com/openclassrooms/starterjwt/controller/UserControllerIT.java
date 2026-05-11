package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("UserController – Integration Tests")
class UserControllerIT extends AbstractIntegrationTest {

    // ─── GET /api/user/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("findById – should return 200 with user data when found")
    void findById_whenFound_shouldReturn200() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        User user = userRepository.findByEmail("user@test.com").orElseThrow();

        mockMvc.perform(get("/api/user/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    @DisplayName("findById – should return 404 when user not found")
    void findById_whenNotFound_shouldReturn404() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/user/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("findById – should return 400 when id is not numeric")
    void findById_invalidId_shouldReturn400() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/user/abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("findById – should return 401 when not authenticated")
    void findById_notAuthenticated_shouldReturn401() throws Exception {

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

    // ─── DELETE /api/user/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("delete – should return 200 and delete user when deleting own account")
    void delete_ownAccount_shouldReturn200AndDelete() throws Exception {

        String token = createUserAndGetToken("owner@test.com", "password", false);

        User user = userRepository.findByEmail("owner@test.com").orElseThrow();

        mockMvc.perform(delete("/api/user/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // ✅ Vérifie suppression réelle (IMPORTANT pour coverage)
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("delete – should return 401 when deleting another user's account")
    void delete_anotherUserAccount_shouldReturn401() throws Exception {

        User victim = User.builder()
                .email("victim@test.com")
                .firstName("Victim")
                .lastName("User")
                .password(passwordEncoder.encode("pass"))
                .admin(false)
                .build();
        victim = userRepository.save(victim);

        String token = createUserAndGetToken("attacker@test.com", "password", false);

        mockMvc.perform(delete("/api/user/" + victim.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

        // ✅ Vérifie que l'utilisateur existe toujours
        assertThat(userRepository.findById(victim.getId())).isPresent();
    }

    @Test
    @DisplayName("delete – should return 401 when admin deletes another user")
    void delete_adminDeletesUser_shouldReturn401() throws Exception {

        User victim = User.builder()
                .email("victim@test.com")
                .firstName("Victim")
                .lastName("User")
                .password(passwordEncoder.encode("pass"))
                .admin(false)
                .build();
        victim = userRepository.save(victim);

        String adminToken = createUserAndGetToken("admin@test.com", "password", true);

        mockMvc.perform(delete("/api/user/" + victim.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnauthorized());

        assertThat(userRepository.findById(victim.getId())).isPresent();
    }

    @Test
    @DisplayName("delete – should return 404 when user not found")
    void delete_userNotFound_shouldReturn404() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(delete("/api/user/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete – should return 401 when not authenticated")
    void delete_notAuthenticated_shouldReturn401() throws Exception {

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete – should return 400 when id is not numeric")
    void delete_invalidId_shouldReturn400() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(delete("/api/user/notanumber")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
