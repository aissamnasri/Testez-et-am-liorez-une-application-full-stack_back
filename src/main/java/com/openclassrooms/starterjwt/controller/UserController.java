package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.service.interfaces.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User resources.
 *
 * The ownership check before deletion has been moved into IUserService.delete(),
 * keeping this controller free of business logic.
 * All exceptions are handled globally by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserMapper userMapper;
    private final IUserService userService;

    public UserController(IUserService userService, UserMapper userMapper) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        User user = this.userService.findById(Long.valueOf(id));
        return ResponseEntity.ok().body(this.userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        this.userService.delete(Long.valueOf(id), userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
