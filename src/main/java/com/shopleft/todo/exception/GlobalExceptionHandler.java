package com.shopleft.todo.exception;

import java.util.NoSuchElementException;

import com.shopleft.todo.exception.custom.UserAlreadyExistsException;
import com.shopleft.todo.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
        return buildErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse("RESOURCE_CONFLICT", ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleWrongCredentials(SecurityException ex, HttpServletRequest request) {
        return buildErrorResponse("AUTHENTICATION_FAILED", "Invalid credentials", HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        DateTimeParseException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        if (ex instanceof MissingServletRequestParameterException missingParamEx) {
            String message = "Missing required parameter: " + missingParamEx.getParameterName();
            return buildErrorResponse("MISSING_PARAMETER", message, HttpStatus.BAD_REQUEST, request);
        }

        if (ex instanceof MethodArgumentTypeMismatchException typeMismatchEx) {
            String message = "Invalid value for parameter: " + typeMismatchEx.getName();
            return buildErrorResponse("INVALID_PARAMETER_TYPE", message, HttpStatus.BAD_REQUEST, request);
        }

        if (ex instanceof DateTimeParseException) {
            return buildErrorResponse("INVALID_DATE_FORMAT", "Date must be in yyyy-MM-dd format", HttpStatus.BAD_REQUEST, request);
        }

        if (ex instanceof HttpMessageNotReadableException) {
            return buildErrorResponse("MALFORMED_REQUEST_BODY", "Malformed JSON request body", HttpStatus.BAD_REQUEST, request);
        }

        return buildErrorResponse("BAD_REQUEST", "Invalid request data", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllErrors(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception while processing request: {}", request.getRequestURI(), ex);
        return buildErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Something went wrong",
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
        String errorCode,
        String message,
        HttpStatus status,
        HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, message, status.value(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }
}
