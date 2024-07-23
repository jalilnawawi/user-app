package com.example.user_app.service;

import com.example.user_app.dto.RegisterUserDto;
import com.example.user_app.model.ERole;
import com.example.user_app.model.Role;
import com.example.user_app.model.User;
import com.example.user_app.repository.RoleRepository;
import com.example.user_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpGenerator otpGenerator;

    @Autowired
    MailService mailService;

    public User register(RegisterUserDto registerUserDto){
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        Set<String> strRoles = registerUserDto.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
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
        user.setRoles(roles);

        userRepository.save(user);

        return user;
    }

    public String forgotPassword(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()){
            return "email doesn't exist";
        } else {
            User user = userOptional.get();
            user.setPassword("has reset");
            user.setOtp(otpGenerator.generateOtp());

            mailService.sendMail(
                    email,
                    "OTP for create new password",
                    "Input this OTP to set up new password " + user.getOtp()
            );
            userRepository.save(user);
        }

        return "password has been reset";
    }

    public String setNewPassword(String email, String password, String otp){
        User user = userRepository.findByEmail(email).get();
        if (user.getPassword().equalsIgnoreCase("has reset")
                &&
                otp.equals(user.getOtp())
        ){
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } else {
            return "password not reset";
        }
        return "success set new password";
    }
}
