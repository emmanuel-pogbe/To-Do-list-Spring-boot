package com.shopleft.todo.dto;

import java.time.LocalDate;

public class UpdatedTask {
    private Long id;
    private String task;
    private LocalDate createdAt;
    private boolean isUpdated;

    public UpdatedTask() {}
    public UpdatedTask(Long id, String task, LocalDate createdAt, boolean isUpdated) {
        this.id = id;
        this.task = task;
        this.createdAt = createdAt;
        this.isUpdated = isUpdated;
    }

    // getters and setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getTask() {return task;}
    public void setTask(String task) {this.task = task;}

    public LocalDate getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDate createdAt) {this.createdAt = createdAt;}

    public boolean getIsUpdated() {return isUpdated;}
    public void setIsUpdated(boolean isUpdated) {this.isUpdated = isUpdated;}
}
