package com.example.user_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutOauth2Controller {
    @GetMapping("/logout-page")
    public String logout() {
        System.out.println("Logout");
        return "logout";
    }
}
