package com.openclassrooms.starterjwt.service;

import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.entity.Teacher;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService – Unit Tests")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("encoded")
                .admin(false)
                .build();

        session = Session.builder()
                .id(1L)
                .name("Yoga Morning")
                .date(new Date())
                .description("A morning yoga session")
                .teacher(Teacher.builder().id(1L).firstName("Alice").lastName("Smith").build())
                .users(new ArrayList<>())
                .build();
    }

    // ─── create ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("create – should save and return the session")
    void create_shouldSaveSession() {
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session result = sessionService.create(session);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga Morning");
        verify(sessionRepository).save(session);
    }

    // ─── delete ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete – should delete when session exists")
    void delete_whenSessionExists_shouldDelete() {
        when(sessionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sessionRepository).deleteById(1L);

        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete – should throw NotFoundException when session does not exist")
    void delete_whenSessionNotFound_shouldThrowNotFoundException() {
        when(sessionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> sessionService.delete(99L))
                .isInstanceOf(NotFoundException.class);
        verify(sessionRepository, never()).deleteById(any());
    }

    // ─── findAll ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll – should return all sessions")
    void findAll_shouldReturnAllSessions() {
        when(sessionRepository.findAll()).thenReturn(List.of(session));

        List<Session> result = sessionService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Yoga Morning");
    }

    @Test
    @DisplayName("findAll – should return empty list when no sessions")
    void findAll_shouldReturnEmptyListWhenNoSessions() {
        when(sessionRepository.findAll()).thenReturn(List.of());

        List<Session> result = sessionService.findAll();

        assertThat(result).isEmpty();
    }

    // ─── getById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById – should return session when found")
    void getById_whenFound_shouldReturnSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getById – should throw NotFoundException when not found")
    void getById_whenNotFound_shouldThrowNotFoundException() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── update ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("update – should update and return session")
    void update_whenSessionExists_shouldUpdate() {
        Session updated = Session.builder()
                .name("Evening Yoga")
                .date(new Date())
                .description("Updated description")
                .users(new ArrayList<>())
                .build();
        when(sessionRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.save(any(Session.class))).thenReturn(updated);

        Session result = sessionService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Evening Yoga");
        verify(sessionRepository).save(updated);
    }

    @Test
    @DisplayName("update – should throw NotFoundException when session not found")
    void update_whenNotFound_shouldThrowNotFoundException() {
        when(sessionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> sessionService.update(99L, session))
                .isInstanceOf(NotFoundException.class);
        verify(sessionRepository, never()).save(any());
    }

    // ─── participate ──────────────────────────────────────────────────────

    @Test
    @DisplayName("participate – should add user to session")
    void participate_whenNotAlreadyParticipating_shouldAddUser() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(1L, 1L);

        assertThat(session.getUsers()).contains(user);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("participate – should throw BadRequestException when already participating")
    void participate_whenAlreadyParticipating_shouldThrowBadRequest() {
        session.setUsers(new ArrayList<>(List.of(user)));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("participate – should throw NotFoundException when session not found")
    void participate_whenSessionNotFound_shouldThrowNotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(99L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("participate – should throw NotFoundException when user not found")
    void participate_whenUserNotFound_shouldThrowNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── noLongerParticipate ──────────────────────────────────────────────

    @Test
    @DisplayName("noLongerParticipate – should remove user from session")
    void noLongerParticipate_whenParticipating_shouldRemoveUser() {
        session.setUsers(new ArrayList<>(List.of(user)));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("noLongerParticipate – should throw BadRequestException when not participating")
    void noLongerParticipate_whenNotParticipating_shouldThrowBadRequest() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session)); // users list is empty

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("noLongerParticipate – should throw NotFoundException when session not found")
    void noLongerParticipate_whenSessionNotFound_shouldThrowNotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.noLongerParticipate(99L, 1L))
                .isInstanceOf(NotFoundException.class);
    }
}
