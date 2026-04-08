package com.shopleft.todo.response;

// will make this more robust later
public class SuccessResponse {
    private String message;
    
    public SuccessResponse(String message) {
        this.message = message;
    }


    // getters and setters - can use lombok but I like writing it :)
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message=message;}
    
}
