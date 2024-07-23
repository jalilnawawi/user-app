package com.example.user_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginOauth2Response {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public LoginOauth2Response(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}
