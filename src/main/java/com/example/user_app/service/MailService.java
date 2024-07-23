package com.example.user_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    Environment env;

    @Autowired
    JavaMailSender javaMailSender;

    public void sendMail(String emailReceiver, String title, String emailMessage){
        SimpleMailMessage message =new SimpleMailMessage();
        message.setFrom(env.getProperty("spring.mail.username"));
        message.setTo(emailReceiver);
        message.setText(emailMessage);
        message.setSubject(title);
        javaMailSender.send(message);
    }
}
