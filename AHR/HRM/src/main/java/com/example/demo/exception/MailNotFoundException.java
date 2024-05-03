package com.example.demo.exception;

public class MailNotFoundException extends RuntimeException {


    public MailNotFoundException(String message) {
        super(message);
    }
}