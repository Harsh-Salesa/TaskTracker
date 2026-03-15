package com.todo.todoApp.controller;

import com.todo.todoApp.entity.Team;
import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.TeamRepository;
import com.todo.todoApp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository,
                           TeamRepository teamRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String role = (String) session.getAttribute("userRole");

        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/login";
        }

        long totalUsers = userRepository.count();

        model.addAttribute("totalUsers", totalUsers);

        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("teams", teamRepository.findAll());

        return "admin-users";
    }

    @GetMapping("/promote/{id}")
    public String promote(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();

        user.setRole("MANAGER");

        userRepository.save(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/demote/{id}")
    public String demote(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();

        user.setRole("USER");

        userRepository.save(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();

        user.setActive(false);

        userRepository.save(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/activate/{id}")
    public String activateUser(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();

        user.setActive(true);

        userRepository.save(user);

        return "redirect:/admin/users";
    }

    // ---------------- TEAM MANAGEMENT ----------------

    @GetMapping("/teams")
    public String teams(Model model){

        model.addAttribute("teams", teamRepository.findAll());

        model.addAttribute("managers",
                userRepository.findByRole("MANAGER"));

        return "admin-teams";
    }

    @PostMapping("/teams/create")
    public String createTeam(@RequestParam String name,
                             @RequestParam Long managerId) {

        User manager = userRepository.findById(managerId).orElseThrow();

        Team team = new Team();
        team.setName(name);
        team.setManager(manager);

        teamRepository.save(team);

        return "redirect:/admin/teams";
    }
    @GetMapping("/create-user")
    public String createUserPage(){
        return "admin-create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password){

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        user.setRole("USER");
        user.setActive(true);

        userRepository.save(user);

        return "redirect:/admin/users";
    }
    @PostMapping("/assign-team")
    public String assignTeam(@RequestParam Long userId,
                             @RequestParam Long teamId){

        User user = userRepository.findById(userId).orElseThrow();

        Team team = teamRepository.findById(teamId).orElseThrow();

        user.setTeam(team);

        userRepository.save(user);

        return "redirect:/admin/users";
    }

}