package com.shopleft.todo.dto;

import java.time.LocalDate;

public class DeletedTask {
    private Long id;
    private String task;
    private LocalDate createdAt;
    private boolean isDeleted;

    public DeletedTask() {}
    public DeletedTask(Long id, String task, LocalDate createdAt, boolean isDeleted) {
        this.id = id;
        this.task = task;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    // getters and setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getTask() {return task;}
    public void setTask(String task) {this.task = task;}

    public LocalDate getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDate createdAt) {this.createdAt = createdAt;}

    public boolean isDeleted() {return isDeleted;}
    public void setDeleted(boolean isDeleted) {this.isDeleted = isDeleted;}
}
