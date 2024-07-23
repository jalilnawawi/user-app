package com.example.user_app.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklist {
    private Set<String> blacklist = new HashSet<>();

    public void addToBlacklist(String token){
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token){
        return blacklist.contains(token);
    }
}
