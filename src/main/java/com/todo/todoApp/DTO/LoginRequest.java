package com.todo.todoApp.DTO;

import jakarta.validation.constraints.*;

public class LoginRequest {

    @NotBlank(message = "Username or Email is required")
    private String loginInput;


    @NotBlank(message = "Password is required")
    private String password;

    public String getLoginInput() {
        return loginInput;
    }

    public void setLoginInput(String loginInput) {
        this.loginInput = loginInput;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
