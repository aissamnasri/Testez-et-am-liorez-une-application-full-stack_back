package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.entity.Teacher;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.service.interfaces.ITeacherService;
import com.openclassrooms.starterjwt.service.interfaces.IUserService;

@DisplayName("SessionMapper – Unit Tests")
class SessionMapperTest {

    private final ITeacherService teacherService = mock(ITeacherService.class);
    private final IUserService userService = mock(IUserService.class);

    private final SessionMapper mapper = Mappers.getMapper(SessionMapper.class);

    // Injection manuelle des mocks
    public SessionMapperTest() {
        mapper.teacherService = teacherService;
        mapper.userService = userService;
    }

    @Test
    @DisplayName("toEntity – should map dto to entity with teacher and users")
    void toEntity_shouldMapAllFields() {

        // GIVEN
        SessionDto dto = new SessionDto();
        dto.setName("Yoga");
        dto.setDescription("Relax session");
        dto.setTeacher_id(1L);
        dto.setUsers(List.of(10L, 20L));

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .build();

        User user1 = User.builder()
                .id(10L)
                .email("u1@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        User user2 = User.builder()
                .id(20L)
                .email("u2@test.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(10L)).thenReturn(user1);
        when(userService.findById(20L)).thenReturn(user2);

        // WHEN
        Session entity = mapper.toEntity(dto);

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Yoga");
        assertThat(entity.getDescription()).isEqualTo("Relax session");
        assertThat(entity.getTeacher()).isEqualTo(teacher);
        assertThat(entity.getUsers()).hasSize(2);

        verify(teacherService).findById(1L);
        verify(userService).findById(10L);
        verify(userService).findById(20L);
    }

    @Test
    @DisplayName("toEntity – should handle null teacher_id")
    void toEntity_nullTeacher_shouldReturnNullTeacher() {

        SessionDto dto = new SessionDto();
        dto.setTeacher_id(null);

        Session entity = mapper.toEntity(dto);

        assertThat(entity.getTeacher()).isNull();
    }

    @Test
    @DisplayName("toEntity – should handle null users list")
    void toEntity_nullUsers_shouldReturnEmptyList() {

        SessionDto dto = new SessionDto();
        dto.setUsers(null);

        Session entity = mapper.toEntity(dto);

        assertThat(entity.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("toDto – should map entity to dto")
    void toDto_shouldMapEntityToDto() {

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .build();

        User user1 = User.builder()
                .id(10L)
                .email("u1@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        User user2 = User.builder()
                .id(20L)
                .email("u2@test.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Session session = Session.builder()
                .name("Yoga")
                .description("Relax")
                .teacher(teacher)
                .users(List.of(user1, user2))
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Yoga");
        assertThat(dto.getDescription()).isEqualTo("Relax");
        assertThat(dto.getTeacher_id()).isEqualTo(1L);
        assertThat(dto.getUsers()).containsExactly(10L, 20L);
    }

    @Test
    @DisplayName("toDto – should handle null users")
    void toDto_nullUsers_shouldReturnEmptyList() {

        Session session = Session.builder()
                .users(null)
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("toDto – should handle null teacher")
    void toDto_nullTeacher_shouldReturnNullTeacherId() {

        Session session = Session.builder()
                .teacher(null)
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto.getTeacher_id()).isNull();
    }
}