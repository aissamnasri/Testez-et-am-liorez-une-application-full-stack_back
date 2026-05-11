package com.openclassrooms.starterjwt.service;

import com.openclassrooms.starterjwt.entity.Teacher;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.service.impl.TeacherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService – Unit Tests")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        teacher1 = Teacher.builder().id(1L).firstName("Alice").lastName("Martin").build();
        teacher2 = Teacher.builder().id(2L).firstName("Bob").lastName("Dupont").build();
    }

    // ─── findAll ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll – should return all teachers")
    void findAll_shouldReturnAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher1, teacher2));

        List<Teacher> result = teacherService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Teacher::getFirstName).containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    @DisplayName("findAll – should return empty list when no teachers")
    void findAll_shouldReturnEmptyList() {
        when(teacherRepository.findAll()).thenReturn(List.of());

        List<Teacher> result = teacherService.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById – should return teacher when found")
    void findById_whenFound_shouldReturnTeacher() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        Teacher result = teacherService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("findById – should throw NotFoundException when not found")
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
