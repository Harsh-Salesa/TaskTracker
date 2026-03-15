package com.todo.todoApp.controller;

import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.UserRepository;
import com.todo.todoApp.service.TodoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

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
    public String viewTeamTasks(Authentication auth, Model model){

        User manager = userRepository
                .findByEmail(auth.getName())
                .orElseThrow();

        model.addAttribute("todos",
                todoService.getTasksByManager(manager));

        model.addAttribute("users",
                userRepository.findByTeamManager(manager));

        return "manager-tasks";
    }

    // assign task to team member
    @PostMapping("/assign")
    public String assignTask(@RequestParam Long taskId,
                             @RequestParam Long userId){

        todoService.assignTask(taskId, userId);

        return "redirect:/manager/tasks";
    }
}