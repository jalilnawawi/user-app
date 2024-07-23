package com.example.user_app.controller;

import com.example.user_app.dto.LoginResponse;
import com.example.user_app.dto.LoginUserDto;
import com.example.user_app.dto.RegisterUserDto;
import com.example.user_app.model.User;
import com.example.user_app.repository.UserRepository;
import com.example.user_app.security.UserDetailsImpl;
import com.example.user_app.service.AuthService;
import com.example.user_app.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth")
@RestController
public class AuthController {
    @Autowired
    JwtService jwtService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto){
        User registerUser = authService.register(registerUserDto);
        return ResponseEntity.ok(registerUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginUserDto){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                                loginUserDto.getUsername(),
                                loginUserDto.getPassword()
                        )
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();
        if (user != null && !user.isActive()){
            user.setActive(true);
            userRepository.save(user);
        }

        LoginResponse loginResponse = new LoginResponse(
            jwt, userDetails.getUsername(), roles, userDetails.isEnabled()
        );

        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/forgot/password")
    public String forgotPassword(@RequestParam String email){
        return authService.forgotPassword(email);
    }

    @PutMapping("/new/password")
    public String setNewPassword(@RequestParam String email, @RequestParam String password, @RequestParam String otp){
        return authService.setNewPassword(email, password, otp);
    }

//    @PostMapping
//    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest){
//        String token = extr
//    }


}
