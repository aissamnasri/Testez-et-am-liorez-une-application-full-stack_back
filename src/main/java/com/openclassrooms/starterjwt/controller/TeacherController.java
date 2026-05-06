package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.entity.Teacher;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.service.interfaces.ITeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Teacher resources.
 *
 * Pure routing: delegates all business logic to ITeacherService.
 * All exceptions are handled globally by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherMapper teacherMapper;
    private final ITeacherService teacherService;

    public TeacherController(ITeacherService teacherService, TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
        this.teacherService = teacherService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        Teacher teacher = this.teacherService.findById(Long.valueOf(id));
        return ResponseEntity.ok().body(this.teacherMapper.toDto(teacher));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Teacher> teachers = this.teacherService.findAll();
        return ResponseEntity.ok().body(this.teacherMapper.toDto(teachers));
    }
}
