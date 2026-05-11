package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthEntryPointJwt – Unit Tests")
class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
    }

    @Test
    @DisplayName("commence – should return HTTP 401 with JSON body")
    void commence_shouldReturn401WithJsonBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/session");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(
                request,
                response,
                new BadCredentialsException("Full authentication is required"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).contains("application/json");

        String body = response.getContentAsString();
        assertThat(body).contains("401");
        assertThat(body).contains("Unauthorized");
        assertThat(body).contains("/api/session");
    }

    @Test
    @DisplayName("commence – should include error message in JSON body")
    void commence_shouldIncludeErrorMessageInBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(
                request,
                response,
                new BadCredentialsException("Bad credentials"));

        String body = response.getContentAsString();
        assertThat(body).contains("Bad credentials");
    }
}
