package com.openclassrooms.starterjwt.service.interfaces;

import com.openclassrooms.starterjwt.entity.Teacher;

import java.util.List;

/**
 * Contract for all business operations on Teacher entities.
 * Implementations live in com.openclassrooms.starterjwt.service.impl.
 */
public interface ITeacherService {
    List<Teacher> findAll();
    Teacher findById(Long id);
}
