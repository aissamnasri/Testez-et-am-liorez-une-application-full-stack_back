package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.entity.User;

@DisplayName("UserMapper – Unit Tests")
class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toDto – should map entity to dto")
    void toDto_shouldMapEntityToDto() {

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(true)
                .build();

        UserDto dto = mapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("toEntity – should map dto to entity")
    void toEntity_shouldMapDtoToEntity() {

        UserDto dto = new UserDto();
        dto.setEmail("test@test.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("password");
        dto.setAdmin(true);

        User entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo("test@test.com");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("toDto – should return null when entity is null")
    void toDto_null_shouldReturnNull() {

        assertThat(mapper.toDto((User) null)).isNull();
    }

    @Test
    @DisplayName("toEntity – should return null when dto is null")
    void toEntity_null_shouldReturnNull() {

        assertThat(mapper.toEntity((UserDto) null)).isNull();
    }

    @Test
    @DisplayName("toDto list – should map list correctly")
    void toDto_list_shouldMap() {

        List<User> users = List.of(

                User.builder()
                        .id(1L)
                        .email("a@test.com")
                        .firstName("John")
                        .lastName("Doe")
                        .password("password")
                        .admin(false)
                        .build(),

                User.builder()
                        .id(2L)
                        .email("b@test.com")
                        .firstName("Jane")
                        .lastName("Smith")
                        .password("password")
                        .admin(false)
                        .build()
        );

        List<UserDto> result = mapper.toDto(users);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("a@test.com");
        assertThat(result.get(1).getEmail()).isEqualTo("b@test.com");
    }

    @Test
    @DisplayName("toEntity list – should map list correctly")
    void toEntity_list_shouldMap() {

        UserDto d1 = new UserDto();
        d1.setEmail("a@test.com");
        d1.setFirstName("John");
        d1.setLastName("Doe");
        d1.setPassword("password");
        d1.setAdmin(false);

        UserDto d2 = new UserDto();
        d2.setEmail("b@test.com");
        d2.setFirstName("Jane");
        d2.setLastName("Smith");
        d2.setPassword("password");
        d2.setAdmin(false);

        List<User> result = mapper.toEntity(List.of(d1, d2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("a@test.com");
        assertThat(result.get(1).getEmail()).isEqualTo("b@test.com");
    }

    @Test
    @DisplayName("toDto list – empty list")
    void toDto_empty_shouldReturnEmpty() {

        assertThat(mapper.toDto(List.of())).isEmpty();
    }

    @Test
    @DisplayName("toEntity list – empty list")
    void toEntity_empty_shouldReturnEmpty() {

        assertThat(mapper.toEntity(List.of())).isEmpty();
    }
}