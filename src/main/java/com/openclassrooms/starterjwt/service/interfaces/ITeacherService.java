package com.openclassrooms.starterjwt.service.interfaces;

import com.openclassrooms.starterjwt.entity.Teacher;

import java.util.List;

public interface ITeacherService {
    List<Teacher> findAll();
    Teacher findById(Long id);
}
