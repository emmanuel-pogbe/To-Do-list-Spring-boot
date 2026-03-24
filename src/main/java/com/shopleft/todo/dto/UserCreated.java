package com.shopleft.todo.dto;

public class UserCreated {
    private Long userId;
    private String userName;
    private boolean createStatus;

    public UserCreated() {}
    public UserCreated(Long userId, String userName,boolean createStatus) {
        this.userId = userId;
        this.userName = userName;
        this.createStatus = createStatus;
    }

    // getters and setters
    public Long getUserId() {return this.userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUserName() {return this.userName;}
    public void setUserName(String userName) {this.userName = userName;}
    
    public boolean getCreateStatus() {return this.createStatus;}
    public void setCreateStatus(boolean createStatus) {this.createStatus = createStatus;}
}
