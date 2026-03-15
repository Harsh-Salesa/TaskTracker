package com.todo.todoApp.controller;

import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){

        String role = (String) session.getAttribute("userRole");

        // 🔒 admin check
        if(role == null || !role.equals("ADMIN")){
            return "redirect:/login";
        }

        long totalUsers = userRepository.count();

        model.addAttribute("totalUsers", totalUsers);

        return "admin-dashboard";
    }
    @GetMapping("/users")
    public String users(Model model){

        model.addAttribute("users", userRepository.findAll());

        return "admin-users.html";
    }
    @GetMapping("/promote/{id}")
    public String promote(@PathVariable Long id){

        User user = userRepository.findById(id).orElseThrow();

        user.setRole("MANAGER");

        userRepository.save(user);

        return "redirect:/admin/users";
    }
}