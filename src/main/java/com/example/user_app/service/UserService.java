package com.example.user_app.service;

import com.example.user_app.dto.UserResponse;
import com.example.user_app.model.User;
import com.example.user_app.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public UserResponse getUserById(UUID id){
        Optional<User> findUserById = userRepository.findById(id);
        UserResponse userResponse = modelMapper.map(findUserById, UserResponse.class);

        return userResponse;
    }

}
