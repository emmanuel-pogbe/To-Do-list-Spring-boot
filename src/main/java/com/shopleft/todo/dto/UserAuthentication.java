package com.shopleft.todo.dto;

public class UserAuthentication {
    private String userName;
    private String password;

    public UserAuthentication() {}
    public UserAuthentication(String userName, String password) {
        this.userName = userName;
        this.password = password;

    }

    // getters and setters
    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}
}
