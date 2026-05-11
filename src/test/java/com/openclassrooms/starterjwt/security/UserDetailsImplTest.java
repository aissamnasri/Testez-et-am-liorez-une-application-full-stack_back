package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDetailsImpl – Unit Tests")
class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("getAuthorities – should return empty collection")
    void getAuthorities_shouldReturnEmpty() {
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("isAccountNonExpired – should return true")
    void isAccountNonExpired_shouldReturnTrue() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    @DisplayName("isAccountNonLocked – should return true")
    void isAccountNonLocked_shouldReturnTrue() {
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("isCredentialsNonExpired – should return true")
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("isEnabled – should return true")
    void isEnabled_shouldReturnTrue() {
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("equals – same id should be equal")
    void equals_sameId_shouldBeEqual() {
        UserDetailsImpl other = UserDetailsImpl.builder()
                .id(1L)
                .username("different@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .admin(true)
                .password("other")
                .build();

        assertThat(userDetails).isEqualTo(other);
    }

    @Test
    @DisplayName("equals – different id should not be equal")
    void equals_differentId_shouldNotBeEqual() {
        UserDetailsImpl other = UserDetailsImpl.builder()
                .id(2L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("encodedPassword")
                .build();

        assertThat(userDetails).isNotEqualTo(other);
    }

    @Test
    @DisplayName("equals – same object reference should be equal")
    void equals_sameReference_shouldBeEqual() {
        assertThat(userDetails).isEqualTo(userDetails);
    }

    @Test
    @DisplayName("equals – null should not be equal")
    void equals_null_shouldNotBeEqual() {
        assertThat(userDetails).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals – different class should not be equal")
    void equals_differentClass_shouldNotBeEqual() {
        assertThat(userDetails).isNotEqualTo("not a UserDetailsImpl");
    }

    @Test
    @DisplayName("getters – should return correct values")
    void getters_shouldReturnCorrectValues() {
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("user@test.com");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getAdmin()).isFalse();
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
    }
}
