package com.shopleft.todo.dto;

import java.util.List;

import com.shopleft.todo.model.Task;

public class UserTasks {
    private Long userId;
    private List<Task> userTasks;

    public UserTasks() {}
    public UserTasks(Long userId, List<Task> userTasks) {
        this.userId = userId;
        this.userTasks = userTasks;
    } 

    // getters and setters
    public Long getUserId() {return this.userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public List<Task> getUserTasks() {return this.userTasks;}
    public void setUserTasks(List<Task> userTasks) {this.userTasks = userTasks;}
    
}
