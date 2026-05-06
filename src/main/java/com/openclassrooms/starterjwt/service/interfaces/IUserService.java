package com.openclassrooms.starterjwt.service.interfaces;

import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.entity.User;

/**
 * Contract for all business operations on User entities.
 * Implementations live in com.openclassrooms.starterjwt.service.impl.
 */
public interface IUserService {
    User findById(Long id);
    User findByEmail(String email);
    void delete(Long id, String requestingUserEmail);
    boolean existsByEmail(String email);
User register(SignupRequest request);
}
