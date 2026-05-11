package com.openclassrooms.starterjwt.dto;

import com.openclassrooms.starterjwt.dto.response.JwtResponse;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Response DTOs - Unit Tests")
class ResponseDtoTest {

    @Test
    @DisplayName("JwtResponse - should expose authentication payload")
    void jwtResponse_shouldExposeAuthenticationPayload() {
        JwtResponse response = new JwtResponse(
                "jwt-token",
                1L,
                "admin@test.com",
                "Admin",
                "User",
                true);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("admin@test.com");
        assertThat(response.getFirstName()).isEqualTo("Admin");
        assertThat(response.getLastName()).isEqualTo("User");
        assertThat(response.getAdmin()).isTrue();
    }

    @Test
    @DisplayName("MessageResponse - should expose message")
    void messageResponse_shouldExposeMessage() {
        MessageResponse response = new MessageResponse("Operation successful");

        assertThat(response.getMessage()).isEqualTo("Operation successful");
    }
}
