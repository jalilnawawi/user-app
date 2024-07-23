package com.example.user_app.dto;

import java.util.Set;

public class UpdateProfileResponseDto {
    private String email;
    private Set<String> roles;

    public UpdateProfileResponseDto(String email, Set<String> roles) {
        this.email = email;
        this.roles = roles;
    }
}
