package com.todo.todoApp.service;

import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtService jwtService) {

        this.passwordEncoder = passwordEncoder;
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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }
}