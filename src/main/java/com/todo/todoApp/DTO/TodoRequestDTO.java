package com.todo.todoApp.DTO;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class TodoRequestDTO {


    @NotBlank(message = "Task name is required")
    private String taskname;

    @NotBlank(message = "Description is required")
    @Size(max = 200)
    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @Future(message = "Deadline must be in future")
    private LocalDateTime deadline;

    @NotBlank(message = "Priority is required")
    private String priority;
    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}