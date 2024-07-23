package com.example.user_app.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateProfileRequestDto {
    private String email;
    private String otp;
    private Set<String> roles;
    private String password;
}
