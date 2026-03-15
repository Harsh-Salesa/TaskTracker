package com.todo.todoApp.service;

import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String login(String loginInput, String password) {

        User user;
        if (loginInput.contains("@")) {
            user = userRepository.findByEmail(loginInput)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepository.findByUsername(loginInput)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        return jwtService.generateToken(user.getEmail());
    }
}