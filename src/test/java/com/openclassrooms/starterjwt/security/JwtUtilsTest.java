package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils – Unit Tests")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    // Base64-encoded 256-bit key (≥256 bits required for HS256)
    private static final String SECRET =
            "dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5Zm9yand0dG9rZW5zaWduaW5n" +
            "dGhpc2lzYXZlcnlsb25nc2VjcmV0a2V5Zm9yand0";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    private Authentication buildAuthentication(String email) {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username(email)
                .firstName("Test")
                .lastName("User")
                .admin(false)
                .password("pass")
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // ─── generateJwtToken ─────────────────────────────────────────────────

    @Test
    @DisplayName("generateJwtToken – should return a non-null token")
    void generateJwtToken_shouldReturnToken() {
        Authentication auth = buildAuthentication("user@test.com");

        String token = jwtUtils.generateJwtToken(auth);

        assertThat(token).isNotNull().isNotEmpty();
    }

    // ─── getUserNameFromJwtToken ───────────────────────────────────────────

    @Test
    @DisplayName("getUserNameFromJwtToken – should extract correct username")
    void getUserNameFromJwtToken_shouldReturnUsername() {
        Authentication auth = buildAuthentication("user@test.com");
        String token = jwtUtils.generateJwtToken(auth);

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(username).isEqualTo("user@test.com");
    }

    // ─── validateJwtToken ─────────────────────────────────────────────────

    @Test
    @DisplayName("validateJwtToken – should return true for a valid token")
    void validateJwtToken_validToken_shouldReturnTrue() {
        String token = jwtUtils.generateJwtToken(buildAuthentication("user@test.com"));

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateJwtToken – should return false for a malformed token")
    void validateJwtToken_malformedToken_shouldReturnFalse() {
        assertThat(jwtUtils.validateJwtToken("not.a.jwt")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken – should return false for a token with wrong signature")
    void validateJwtToken_wrongSignature_shouldReturnFalse() {
        // Generate with a different key
        JwtUtils otherUtils = new JwtUtils();
        ReflectionTestUtils.setField(otherUtils, "jwtSecret",
                "YW5vdGhlcnNlY3JldGtleWZvcnRlc3RpbmdwdXJwb3Nlc29ubHl5eXl5eXl5eQ==");
        ReflectionTestUtils.setField(otherUtils, "jwtExpirationMs", 86400000);

        String tokenFromOtherKey = otherUtils.generateJwtToken(buildAuthentication("user@test.com"));

        assertThat(jwtUtils.validateJwtToken(tokenFromOtherKey)).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken – should return false for an empty string")
    void validateJwtToken_emptyString_shouldReturnFalse() {
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken – should return false for an expired token")
    void validateJwtToken_expiredToken_shouldReturnFalse() {
        JwtUtils shortLived = new JwtUtils();
        ReflectionTestUtils.setField(shortLived, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(shortLived, "jwtExpirationMs", -1000); // already expired

        String expiredToken = shortLived.generateJwtToken(buildAuthentication("user@test.com"));

        assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken – should return false for an unsupported JWT (no signature)")
    void validateJwtToken_unsupportedToken_shouldReturnFalse() {
        // Un JWT non signé (alg=none) est rejeté comme "unsupported"
        String unsignedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ1c2VyQHRlc3QuY29tIn0.";
        assertThat(jwtUtils.validateJwtToken(unsignedToken)).isFalse();
    }
}
