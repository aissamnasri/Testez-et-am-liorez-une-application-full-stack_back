package com.openclassrooms.starterjwt.service.impl;

import com.openclassrooms.starterjwt.entity.Teacher;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.service.interfaces.ITeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic for Teacher entities.
 */
@Service
public class TeacherServiceImpl implements ITeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public List<Teacher> findAll() {
        return this.teacherRepository.findAll();
    }

    /**
     * @throws NotFoundException if no teacher exists with the given id.
     */
    @Override
    public Teacher findById(Long id) {
        return this.teacherRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }
}
