package com.shopleft.todo.response;

import java.time.OffsetDateTime;

public class ErrorResponse {
    private String message;
    private int status;
    private String errorCode;
    private String path;
    private OffsetDateTime timestamp;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.errorCode = "UNSPECIFIED_ERROR";
        this.path = null;
        this.timestamp = OffsetDateTime.now();
    }
    public ErrorResponse(String errorCode, String message, int status, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }
    public String getMessage() {
        return message;
    }
    
    public int getStatus() {
        return status;
    }
    public String getErrorCode() {
        return errorCode;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

}
