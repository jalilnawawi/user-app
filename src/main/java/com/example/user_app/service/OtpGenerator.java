package com.example.user_app.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpGenerator {
    public String generateOtp(){
        Random random = new Random();
        int otp = random.nextInt(1000000);
        return String.valueOf(otp);
    }
}
