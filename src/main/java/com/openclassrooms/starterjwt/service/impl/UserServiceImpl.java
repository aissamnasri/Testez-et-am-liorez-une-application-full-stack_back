package com.openclassrooms.starterjwt.service.impl;

import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void register(SignupRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .admin(false)
                .build();
        userRepository.save(user);
    }

    @Override
    public void delete(Long id, String currentUserEmail) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!user.getEmail().equals(currentUserEmail)) {
            throw new UnauthorizedException();
        }
        userRepository.deleteById(id);
    }
}
