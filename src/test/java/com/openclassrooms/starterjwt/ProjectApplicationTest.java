package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@DisplayName("ProjectApplication - Unit Tests")
class ProjectApplicationTest {

    @Test
    @DisplayName("constructor - should create application instance")
    void constructor_shouldCreateApplicationInstance() {
        assertThat(new ProjectApplication()).isNotNull();
    }

    @Test
    @DisplayName("main - should delegate to SpringApplication")
    void main_shouldDelegateToSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ProjectApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ProjectApplication.class, args));
        }
    }
}
