package com.shopleft.todo.dto;

public class NewTask {
    private Long userId;
    private String task;

    public NewTask() {}

    public NewTask(Long userId, String task) {
        this.userId = userId;
        this.task = task;
    }

    // getters and setters
    
    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getTask() {return task;}
    public void setTask(String task) {this.task = task;}
}
