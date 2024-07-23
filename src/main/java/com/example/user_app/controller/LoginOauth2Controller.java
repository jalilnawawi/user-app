package com.example.user_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginOauth2Controller {
    @GetMapping("/login")
    public String login() {
        System.out.println("Login");
        return "login";
    }
}
