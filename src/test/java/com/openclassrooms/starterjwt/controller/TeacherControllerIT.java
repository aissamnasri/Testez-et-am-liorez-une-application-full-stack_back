package com.openclassrooms.starterjwt.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.openclassrooms.starterjwt.entity.Teacher;

@DisplayName("TeacherController – Integration Tests")
class TeacherControllerIT extends AbstractIntegrationTest {

    private Teacher saveTeacher(String firstName, String lastName) {
        return teacherRepository.save(Teacher.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build());
    }

    // ─── GET /api/teacher ─────────────────────────────────────────────────

    @Test
    @DisplayName("findAll – should return 200 with list of teachers when authenticated")
    void findAll_authenticated_shouldReturn200WithTeachers() throws Exception {

        saveTeacher("Alice", "Martin");
        saveTeacher("Bob", "Dupont");

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));

        // ✅ Vérification DB (boost JaCoCo)
        assertThat(teacherRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("findAll – should return empty array when no teachers")
    void findAll_noTeachers_shouldReturnEmptyArray() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        assertThat(teacherRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("findAll – should return 401 when not authenticated")
    void findAll_notAuthenticated_shouldReturn401() throws Exception {

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET /api/teacher/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("findById – should return 200 with teacher when found")
    void findById_whenFound_shouldReturn200() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        Teacher teacher = saveTeacher("Alice", "Martin");

        mockMvc.perform(get("/api/teacher/" + teacher.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Martin"));
    }

    @Test
    @DisplayName("findById – should return 404 when teacher not found")
    void findById_whenNotFound_shouldReturn404() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/teacher/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("findById – should return 400 when id is not a number")
    void findById_invalidId_shouldReturn400() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/teacher/abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("findById – should return 404 when id is negative")
    void findById_negativeId_shouldReturn404() throws Exception {

        String token = createUserAndGetToken("user@test.com", "password", false);

        mockMvc.perform(get("/api/teacher/-1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("findById – should return 401 when not authenticated")
    void findById_notAuthenticated_shouldReturn401() throws Exception {

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isUnauthorized());
    }
}
