package com.todo.todoApp.controller;

import com.todo.todoApp.repository.UserRepository;
import com.todo.todoApp.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


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

    // show all tasks
    @GetMapping("/tasks")
    public String viewAllTasks(Model model){

        model.addAttribute("todos", todoService.getAllTasks());
        model.addAttribute("users", userRepository.findAll());

        return "manager-tasks";
    }

    // assign task
    @PostMapping("/assign")
    public String assignTask(@RequestParam Long taskId,
                             @RequestParam Long userId){

        todoService.assignTask(taskId, userId);

        return "redirect:/manager/tasks";
    }
}