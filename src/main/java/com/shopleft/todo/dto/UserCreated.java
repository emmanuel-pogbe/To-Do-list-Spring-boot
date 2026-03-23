package com.shopleft.todo.dto;

public class UserCreated {
    private Long userId;
    private String userName;

    public UserCreated() {}
    public UserCreated(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // getters and setters
    public Long getUserId() {return this.userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUserName() {return this.userName;}
    public void setUserName(String userName) {this.userName = userName;} 
}
