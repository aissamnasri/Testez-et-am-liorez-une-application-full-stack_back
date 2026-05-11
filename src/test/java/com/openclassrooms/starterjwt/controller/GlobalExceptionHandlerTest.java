package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.GlobalExceptionHandler;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler – Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleNumberFormatException – should return 400 BAD_REQUEST")
    void handleNumberFormatException_shouldReturn400() {
        ResponseEntity<Void> response = handler.handleNumberFormatException(
                new NumberFormatException("not a number"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("handleBadRequestException – should return 400 BAD_REQUEST")
    void handleBadRequestException_shouldReturn400() {
        ResponseEntity<Void> response = handler.handleBadRequestException(
                new BadRequestException());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("handleNotFoundException – should return 404 NOT_FOUND")
    void handleNotFoundException_shouldReturn404() {
        ResponseEntity<Void> response = handler.handleNotFoundException(
                new NotFoundException());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("handleUnauthorizedException – should return 401 UNAUTHORIZED")
    void handleUnauthorizedException_shouldReturn401() {
        ResponseEntity<Void> response = handler.handleUnauthorizedException(
                new UnauthorizedException());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
