package com.openclassrooms.starterjwt.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.entity.Teacher;
import com.openclassrooms.starterjwt.entity.User;

@DisplayName("SessionController – Integration Tests")
class SessionControllerIT extends AbstractIntegrationTest {

    private Teacher savedTeacher;
    private User savedUser;
    private String userToken;

    @BeforeEach
    void setupData() {
        savedTeacher = teacherRepository.save(
                Teacher.builder()
                        .firstName("Alice")
                        .lastName("Martin")
                        .build()
        );

        userToken = createUserAndGetToken("user@test.com", "password", false);

        savedUser = userRepository.findByEmail("user@test.com").orElseThrow();
    }

    private Session createSession(String name) {
        return sessionRepository.save(Session.builder()
                .name(name)
                .date(new Date())
                .description("Test description for " + name)
                .teacher(savedTeacher)
                .users(new ArrayList<>())
                .build());
    }

    private SessionDto buildSessionDto(String name) {
        SessionDto dto = new SessionDto();
        dto.setName(name);
        dto.setDate(new Date());
        dto.setTeacher_id(savedTeacher.getId());
        dto.setDescription("A test yoga session");
        dto.setUsers(List.of());
        return dto;
    }

    // ─── GET ALL ──────────────────────────────────────────────────────────

    @Test
    void findAll_shouldReturn200WithSessions() throws Exception {
        createSession("Morning Yoga");
        createSession("Evening Yoga");

        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Morning Yoga"))
                .andExpect(jsonPath("$[1].name").value("Evening Yoga"));

        assertThat(sessionRepository.findAll()).hasSize(2);
    }

    @Test
    void findAll_notAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────

    @Test
    void findById_whenFound_shouldReturn200() throws Exception {
        Session session = createSession("Morning Yoga");

        mockMvc.perform(get("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Yoga"));
    }

    @Test
    void findById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/session/99999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_invalidId_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/session/abc")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    // ─── CREATE ───────────────────────────────────────────────────────────

    @Test
    void create_validBody_shouldReturn200() throws Exception {
        SessionDto dto = buildSessionDto("New Yoga");

        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Yoga"));

        assertThat(sessionRepository.findAll()).hasSize(1);
    }

    @Test
    void create_blankName_shouldReturn400() throws Exception {
        SessionDto dto = buildSessionDto("");

        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_notAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildSessionDto("Test"))))
                .andExpect(status().isUnauthorized());
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────

    @Test
    void update_whenFound_shouldReturn200() throws Exception {
        Session session = createSession("Old Name");
        SessionDto dto = buildSessionDto("Updated Name");

        mockMvc.perform(put("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        Session updated = sessionRepository.findById(session.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void update_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(put("/api/session/99999")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildSessionDto("Name"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_invalidId_shouldReturn400() throws Exception {
        mockMvc.perform(put("/api/session/abc")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildSessionDto("Name"))))
                .andExpect(status().isBadRequest());
    }

    // ─── DELETE ───────────────────────────────────────────────────────────

    @Test
    void delete_whenFound_shouldReturn200() throws Exception {
        Session session = createSession("ToDelete");

        mockMvc.perform(delete("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/session/99999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_invalidId_shouldReturn400() throws Exception {
        mockMvc.perform(delete("/api/session/invalid")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    // ─── PARTICIPATE ──────────────────────────────────────────────────────

    @Test
    void participate_shouldReturn200_andAddUser() throws Exception {
        Session session = createSession("Yoga");

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(1))
                .andExpect(jsonPath("$.users[0]").value(savedUser.getId()));
    }

    @Test
    void participate_alreadyParticipating_shouldReturn400() throws Exception {
        Session session = createSession("Yoga");

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + savedUser.getId())
                .header("Authorization", "Bearer " + userToken));

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void participate_notAuthenticated_shouldReturn401() throws Exception {
        Session session = createSession("Yoga");

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + savedUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    // ─── LEAVE SESSION ────────────────────────────────────────────────────

    @Test
    void noLongerParticipate_shouldReturn200_andRemoveUser() throws Exception {
        Session session = createSession("Yoga");

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + savedUser.getId())
                .header("Authorization", "Bearer " + userToken));

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(0));
    }

    @Test
    void noLongerParticipate_notParticipating_shouldReturn400() throws Exception {
        Session session = createSession("Yoga");

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noLongerParticipate_notAuthenticated_shouldReturn401() throws Exception {
        Session session = createSession("Yoga");

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + savedUser.getId()))
                .andExpect(status().isUnauthorized());
    }
}
