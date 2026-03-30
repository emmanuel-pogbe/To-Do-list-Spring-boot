package com.shopleft.todo.exception.custom;

public class WrongCredentialsException extends RuntimeException {
    public WrongCredentialsException(String message) {
        super(message);
    }    
}
