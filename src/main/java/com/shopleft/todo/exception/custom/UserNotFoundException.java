package com.shopleft.todo.exception.custom;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }    
}
