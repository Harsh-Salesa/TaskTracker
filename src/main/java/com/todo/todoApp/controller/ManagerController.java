package com.todo.todoApp.controller;

import com.todo.todoApp.entity.Team;
import com.todo.todoApp.entity.Todo;
import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;
import com.todo.todoApp.service.TodoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final TodoService todoService;
    private final UserRepository userRepository;

    public ManagerController(TodoService todoService,
                             UserRepository userRepository) {
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    // show manager team tasks
    @GetMapping("/tasks")
    public String managerTasks(Model model, HttpSession session){

        String email = (String) session.getAttribute("userEmail");

        User manager = userRepository.findByEmail(email).orElseThrow();

        // manager ki team ke tasks
        List<Todo> todos = todoService.getTasksByManager(manager);

        List<User> teamMembers = userRepository.findByTeam(manager.getTeam());

        model.addAttribute("todos", todos);
        model.addAttribute("users", teamMembers);

        return "manager-tasks";
    }

    // assign task to team member
    @PostMapping("/assign")
    public String assignTask(@RequestParam Long taskId,
                             @RequestParam Long userId){

        todoService.assignTask(taskId, userId);

        return "redirect:/manager/tasks";
    }
    @GetMapping("/team")
    public String viewTeam(Model model, HttpSession session){

        String email = (String) session.getAttribute("userEmail");

        User manager = userRepository.findByEmail(email).orElseThrow();

        Team team = manager.getTeam();

        List<User> members = userRepository.findByTeam(team);

        model.addAttribute("members", members);

        return "manager-team";
    }
}