package com.example.demo.exception;

public class AttendanceExistException extends RuntimeException{
    public AttendanceExistException(String message) {
        super(message);
    }
}
