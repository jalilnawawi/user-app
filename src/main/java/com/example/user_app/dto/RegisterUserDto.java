package com.example.user_app.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterUserDto {
    private String username;
    private String email;
    private String password;

//    private String otp;

    private Set<String> role;

    public RegisterUserDto(String username, String email, String password, Set<String> role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
