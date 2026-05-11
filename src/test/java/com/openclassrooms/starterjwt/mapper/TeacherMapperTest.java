package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.entity.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TeacherMapper – Unit Tests")
class TeacherMapperTest {

    private final TeacherMapper mapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    @DisplayName("toDto – should map entity to dto")
    void toDto_shouldMapEntityToDto() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Martin")
                .build();

        TeacherDto dto = mapper.toDto(teacher);

        assertThat(dto).isNotNull();
        assertThat(dto.getFirstName()).isEqualTo("Alice");
        assertThat(dto.getLastName()).isEqualTo("Martin");
    }

    @Test
    @DisplayName("toEntity – should map dto to entity")
    void toEntity_shouldMapDtoToEntity() {
        TeacherDto dto = new TeacherDto();
        dto.setFirstName("Alice");
        dto.setLastName("Martin");

        Teacher entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo("Alice");
        assertThat(entity.getLastName()).isEqualTo("Martin");
    }

    @Test
    @DisplayName("toDto – null")
    void toDto_null_shouldReturnNull() {
        assertThat(mapper.toDto((Teacher) null)).isNull();
    }

    @Test
    @DisplayName("toEntity – null")
    void toEntity_null_shouldReturnNull() {
        assertThat(mapper.toEntity((TeacherDto) null)).isNull();
    }

    @Test
    @DisplayName("toDto list – should map list")
    void toDto_list_shouldMap() {
        List<Teacher> teachers = List.of(
                Teacher.builder().firstName("A").build(),
                Teacher.builder().firstName("B").build()
        );

        List<TeacherDto> result = mapper.toDto(teachers);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("A");
        assertThat(result.get(1).getFirstName()).isEqualTo("B");
    }

    @Test
    @DisplayName("toEntity list – should map list")
    void toEntity_list_shouldMap() {
        TeacherDto d1 = new TeacherDto();
        d1.setFirstName("A");

        TeacherDto d2 = new TeacherDto();
        d2.setFirstName("B");

        List<Teacher> result = mapper.toEntity(List.of(d1, d2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("A");
        assertThat(result.get(1).getFirstName()).isEqualTo("B");
    }

    @Test
    @DisplayName("toDto empty list")
    void toDto_empty_shouldReturnEmpty() {
        assertThat(mapper.toDto(List.of())).isEmpty();
    }

    @Test
    @DisplayName("toEntity empty list")
    void toEntity_empty_shouldReturnEmpty() {
        assertThat(mapper.toEntity(List.of())).isEmpty();
    }
}