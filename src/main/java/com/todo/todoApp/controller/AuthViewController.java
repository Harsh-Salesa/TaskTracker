package com.todo.todoApp.controller;

import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password){

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);

        return "redirect:/login";
    }

    @PostMapping("/login-form")
    public String login(@RequestParam String logininput,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletRequest request,
                        Model model){
        User dbUser;
        if(logininput.contains("@")){
            dbUser = userRepository
                    .findByEmail(logininput)
                    .orElse(null);
        }
        else {
            dbUser=userRepository.findByUsername(logininput)
                    .orElse(null);
        }

        if(dbUser == null){
            model.addAttribute("error","User not found");
            return "login";
        }

        if(!passwordEncoder.matches(password, dbUser.getPassword())){
            model.addAttribute("error","Invalid password");
            return "login";
        }

        session.setAttribute("userEmail", dbUser.getEmail());
        session.setAttribute("userRole", dbUser.getRole());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        dbUser.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + dbUser.getRole()))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        // 🔴 IMPORTANT
        request.getSession()
                .setAttribute("SPRING_SECURITY_CONTEXT",
                        SecurityContextHolder.getContext());

        if("ADMIN".equals(dbUser.getRole())){
            return "redirect:/admin/dashboard";
        }

        return "redirect:/tasks";
    }
}