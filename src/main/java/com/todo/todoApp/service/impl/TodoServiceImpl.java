package com.todo.todoApp.service.impl;

import com.todo.todoApp.DTO.TodoRequestDTO;
import com.todo.todoApp.entity.Todo;
import com.todo.todoApp.entity.User;
import com.todo.todoApp.repository.TodoRepository;
import com.todo.todoApp.repository.UserRepository;
import com.todo.todoApp.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoServiceImpl(TodoRepository todoRepository,
                           UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    // ================= BASIC TODO =================

    @Override
    public Todo saveTask(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public List<Todo> getAllTasks() {
        return todoRepository.findAll();
    }

    @Override
    public Todo getTaskById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public Todo updateTask(Long id, Todo todo) {

        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existing.setTaskname(todo.getTaskname());
        existing.setDescription(todo.getDescription());
        existing.setStatus(todo.getStatus());
        existing.setDeadline(todo.getDeadline());
        existing.setPriority(todo.getPriority());

        return todoRepository.save(existing);
    }

    @Override
    public void deleteTask(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public List<Todo> filterTasks(Long id, String status) {

        if (status == null || status.isEmpty()) {
            return todoRepository.findAll();
        }

        return todoRepository.findByStatus(status);
    }

    @Override
    public Page<Todo> getTodos(int page) {
        return todoRepository.findAll(PageRequest.of(page, 5));
    }

    // ================= USER TASKS =================

    @Override
    public List<Todo> getTasksByUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return todoRepository.findByCreatedBy(user);
    }

    @Override
    public Todo saveTodo(Todo todo, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        todo.setCreatedBy(user);

        // team auto assign
        todo.setTeam(user.getTeam());

        return todoRepository.save(todo);
    }

    // ================= MANAGER FEATURES =================

    @Override
    public void assignTask(Long taskId, Long userId) {

        Todo todo = todoRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        todo.setAssignedTo(user);

        todoRepository.save(todo);
    }

    @Override
    public List<Todo> getTasksByManager(User manager) {

        return todoRepository.findByTeamManager(manager);
    }
    @Override
    public Todo saveTodoFromDTO(TodoRequestDTO dto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo todo = new Todo();

        todo.setTaskname(dto.getTaskname());
        todo.setDescription(dto.getDescription());
        todo.setStatus(dto.getStatus());
        todo.setPriority(dto.getPriority());
        todo.setDeadline(dto.getDeadline());

        todo.setCreatedBy(user);
        todo.setTeam(user.getTeam());

        return todoRepository.save(todo);
    }

    @Override
    public void updateTaskFromDTO(Long id, TodoRequestDTO dto) {

        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existing.setTaskname(dto.getTaskname());
        existing.setDescription(dto.getDescription());
        existing.setStatus(dto.getStatus());
        existing.setPriority(dto.getPriority());
        existing.setDeadline(dto.getDeadline());

        todoRepository.save(existing);
    }
}