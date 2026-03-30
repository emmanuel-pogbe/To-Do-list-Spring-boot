package com.shopleft.todo.exception.custom;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }       
}
