package com.example.user_app.service;

import com.example.user_app.dto.UpdateProfileRequestDto;
import com.example.user_app.exception.DataInvalidException;
import com.example.user_app.exception.DataNotFoundException;
import com.example.user_app.model.ERole;
import com.example.user_app.model.Role;
import com.example.user_app.model.User;
import com.example.user_app.repository.RoleRepository;
import com.example.user_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class OAuth2Service {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpGenerator otpGenerator;

    @Autowired
    MailService mailService;

    public void createUserfromOauth2(String username, String email){
        Role role = roleRepository.findByName(ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>(Collections.singleton(role));
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isEmpty()){
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword("to be update after login");
            user.setRoles(roles);
            user.setActive(false);
            user.setOtp(otpGenerator.generateOtp());
            userRepository.save(user);
            mailService.sendMail(email,
                    "OTP for your account",
                    "Don't share this OTP " + user.getOtp()
            );
        } else {
            userRepository.findByEmail(email).get();
        }
    }

    public User updateProfile(UpdateProfileRequestDto updateProfileRequestDto){
        User user = userRepository.findByEmail(updateProfileRequestDto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));

        Set<String> strRoles = updateProfileRequestDto.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null){
            Role userRole = roleRepository.findByName(ERole.ROLE_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role){
                    case "merchant" :
                        Role merchantRole = roleRepository.findByName(ERole.ROLE_MERCHANT);
                        roles.add(merchantRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
                        roles.add(userRole);
                }
            });
        }

        if (updateProfileRequestDto.getOtp().equals(user.getOtp())){
            user.setOtp(updateProfileRequestDto.getOtp());
            user.setPassword(passwordEncoder.encode(updateProfileRequestDto.getPassword()));
            user.setRoles(roles);
            user.setActive(true);
            return userRepository.save(user);
        } else {
            throw new DataInvalidException("Invalid OTP");
        }
    }

    public User getByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
}
