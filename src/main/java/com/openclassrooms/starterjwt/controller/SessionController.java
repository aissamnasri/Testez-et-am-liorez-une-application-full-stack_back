package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.service.interfaces.ISessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Session resources.
 *
 * Pure routing: delegates all business logic to ISessionService.
 * All exceptions are handled globally by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/session")
public class SessionController {

    private final SessionMapper sessionMapper;
    private final ISessionService sessionService;

    public SessionController(ISessionService sessionService, SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
        this.sessionService = sessionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        Session session = this.sessionService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(this.sessionMapper.toDto(session));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Session> sessions = this.sessionService.findAll();
        return ResponseEntity.ok().body(this.sessionMapper.toDto(sessions));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SessionDto sessionDto) {
        Session session = this.sessionService.create(this.sessionMapper.toEntity(sessionDto));
        return ResponseEntity.ok().body(this.sessionMapper.toDto(session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id,
                                    @Valid @RequestBody SessionDto sessionDto) {
        Session session = this.sessionService.update(Long.parseLong(id),
                this.sessionMapper.toEntity(sessionDto));
        return ResponseEntity.ok().body(this.sessionMapper.toDto(session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        this.sessionService.delete(Long.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/participate/{userId}")
    public ResponseEntity<?> participate(@PathVariable("id") String id,
                                         @PathVariable("userId") String userId) {
        this.sessionService.participate(Long.parseLong(id), Long.parseLong(userId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/participate/{userId}")
    public ResponseEntity<?> noLongerParticipate(@PathVariable("id") String id,
                                                  @PathVariable("userId") String userId) {
        this.sessionService.noLongerParticipate(Long.parseLong(id), Long.parseLong(userId));
        return ResponseEntity.ok().build();
    }
}
