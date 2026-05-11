package com.openclassrooms.starterjwt.service;

import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService – Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();
    }

    // ─── findById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById – should return user when found")
    void findById_whenFound_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("findById – should throw NotFoundException when not found")
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── findByEmail ──────────────────────────────────────────────────────

    @Test
    @DisplayName("findByEmail – should return user when email exists")
    void findByEmail_whenEmailExists_shouldReturnUser() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("john@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("findByEmail – should return null when email not found")
    void findByEmail_whenEmailNotFound_shouldReturnNull() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        User result = userService.findByEmail("unknown@test.com");

        assertThat(result).isNull();
    }

    // ─── existsByEmail ────────────────────────────────────────────────────

    @Test
    @DisplayName("existsByEmail – should return true when email exists")
    void existsByEmail_whenEmailExists_shouldReturnTrue() {
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThat(userService.existsByEmail("john@test.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail – should return false when email does not exist")
    void existsByEmail_whenEmailNotExists_shouldReturnFalse() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);

        assertThat(userService.existsByEmail("new@test.com")).isFalse();
    }

    // ─── register ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("register – should encode password and save user")
    void register_shouldEncodePasswordAndSave() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(request);

        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(argThat(saved ->
                saved.getEmail().equals("new@test.com")
                && saved.getPassword().equals("hashedPassword")
                && !saved.isAdmin()
        ));
    }

    // ─── delete ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete – should delete when user is owner")
    void delete_whenOwner_shouldDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L, "john@test.com");

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete – should throw UnauthorizedException when not the owner")
    void delete_whenNotOwner_shouldThrowUnauthorized() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.delete(1L, "other@test.com"))
                .isInstanceOf(UnauthorizedException.class);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete – should throw NotFoundException when user not found")
    void delete_whenNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L, "john@test.com"))
                .isInstanceOf(NotFoundException.class);
    }
}
