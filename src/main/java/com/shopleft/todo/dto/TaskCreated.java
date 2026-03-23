package com.shopleft.todo.dto;

public class TaskCreated {
    private Long taskId;
    private String task;

    public TaskCreated() {}
    public TaskCreated(Long taskId, String task) {
        this.taskId = taskId;
        this.task = task;
    }

    // getters and setters
    
    public Long getTaskId() {return taskId;}
    public void setTaskId(Long taskId) {this.taskId = taskId;}

    public String getTask() {return task;}
    public void setTask(String task) {this.task = task;}
}
