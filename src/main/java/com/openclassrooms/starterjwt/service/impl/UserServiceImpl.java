package com.openclassrooms.starterjwt.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.service.interfaces.IUserService;

/**
 * Business logic for User entities.
 *
 * Ownership check for account deletion lives here,
 * keeping controllers free of business rules.
 */
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ← ajouter le champ

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) { // ← ajouter le paramètre
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @throws NotFoundException if no user exists with the given id.
     */
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Deletes the user account identified by {@code id}, after verifying
     * that the requesting user is the account owner.
     *
     * @throws NotFoundException     if no user exists with the given id.
     * @throws UnauthorizedException if the authenticated user is not the owner.
     */
    @Override
    public void delete(Long id, String requestingUserEmail) {
        User user = this.userRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (!user.getEmail().equals(requestingUserEmail)) {
            throw new UnauthorizedException();
        }

        this.userRepository.deleteById(id);
    }
    @Override
public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
}

@Override
public User register(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new BadRequestException(); // ou une exception dédiée EmailAlreadyTakenException
    }
    User user = new User(
        null, request.getEmail(),
        request.getLastName(),
        request.getFirstName(),
        passwordEncoder.encode(request.getPassword()),
        false, null, null);
    return userRepository.save(user);
}
    
}
