package com.todo.todoApp.controller;

import com.todo.todoApp.DTO.LoginRequest;
import com.todo.todoApp.DTO.RegisterRequest;
import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthViewController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthViewController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterRequest request,
                               BindingResult result,
                               Model model){

        // 🔴 Validation errors
        if(result.hasErrors()){
            return "register";
        }

        // 🔴 Duplicate email check
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            result.rejectValue("email", null, "Email already exists");
            return "register";
        }

        // 🔴 Save user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setActive(true);

        userRepository.save(user);

        return "redirect:/login";
    }

    // ================= LOGIN =================
    @PostMapping("/login-form")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest request,
                        BindingResult result,
                        HttpSession session,
                        HttpServletRequest httpRequest,
                        Model model){

        // 🔴 Validation errors
        if(result.hasErrors()){
            return "login";
        }

        User dbUser;

        // Allow login via email OR username
        if(request.getLoginInput().contains("@")){
            dbUser = userRepository
                    .findByEmail(request.getLoginInput())
                    .orElse(null);
        } else {
            dbUser = userRepository
                    .findByUsername(request.getLoginInput())
                    .orElse(null);
        }

        if(dbUser == null){
            model.addAttribute("error","User not found");
            return "login";
        }

        if(!passwordEncoder.matches(request.getPassword(), dbUser.getPassword())){
            model.addAttribute("error","Invalid password");
            return "login";
        }

        // 🔴 Session setup
        session.setAttribute("userEmail", dbUser.getEmail());
        session.setAttribute("userRole", dbUser.getRole());

        // 🔴 Spring Security context
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        dbUser.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + dbUser.getRole()))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        httpRequest.getSession()
                .setAttribute("SPRING_SECURITY_CONTEXT",
                        SecurityContextHolder.getContext());

        if("ADMIN".equals(dbUser.getRole())){
            return "redirect:/admin/dashboard";
        }

        return "redirect:/tasks";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return "redirect:/login";
    }
}