package com.openclassrooms.starterjwt.service.interfaces;

import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.entity.User;

public interface IUserService {
    User findById(Long id);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    void register(SignupRequest signUpRequest);
    void delete(Long id, String currentUserEmail);
}
