package com.example.user_app.exception;

public class DataInvalidException extends RuntimeException{
    public DataInvalidException(String message) {
        super(message);
    }
}
