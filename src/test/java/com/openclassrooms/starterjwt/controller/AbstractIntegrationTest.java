package com.openclassrooms.starterjwt.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

/**
 * Base class for integration tests.
 * Uses H2 in-memory database (profile "test") and full Spring context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TeacherRepository teacherRepository;

    @Autowired
    protected SessionRepository sessionRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtils jwtUtils;

    @BeforeEach
    void cleanDatabase() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Creates a user in DB and returns a valid JWT for that user.
     * ✔ Utilise le vrai user sauvegardé
     * ✔ ID réel (important pour les controllers)
     * ✔ Compatible avec Spring Security
     */
    protected String createUserAndGetToken(String email, String password, boolean admin) {
        // 1. Création utilisateur réel en base
        User user = User.builder()
                .email(email)
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode(password))
                .admin(admin)
                .build();

        user = userRepository.save(user); // ✅ important pour récupérer l'ID réel

        // 2. Construction des UserDetails cohérents avec la DB
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(user.getId()) // ✅ ID réel
                .username(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .admin(user.isAdmin())
                .password(user.getPassword())
                .build();

        // 3. Création de l'authentification Spring Security
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 4. Génération du JWT
        return jwtUtils.generateJwtToken(authentication);
    }
}