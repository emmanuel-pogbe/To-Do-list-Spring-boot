package com.shopleft.todo.exception;

import com.shopleft.todo.exception.custom.*;
import com.shopleft.todo.exception.response.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse newError = new ErrorResponse(ex.getMessage(), 404);
        return new ResponseEntity<>(newError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorResponse newError = new ErrorResponse(ex.getMessage(), 404);
        return new ResponseEntity<>(newError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse newError = new ErrorResponse(ex.getMessage(), 400);
        return new ResponseEntity<>(newError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongCredentialsException.class) 
    public ResponseEntity<ErrorResponse> handleWrongCredentials(WrongCredentialsException ex) {
        ErrorResponse newError = new ErrorResponse(ex.getMessage(), 401);
        return new ResponseEntity<>(newError,HttpStatus.UNAUTHORIZED);
    }
}
