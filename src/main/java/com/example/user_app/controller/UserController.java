package com.example.user_app.controller;

import com.example.user_app.dto.UserResponse;
import com.example.user_app.model.User;
import com.example.user_app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        return new ResponseEntity<>(userService.allUsers(), HttpStatus.OK);
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id){
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
