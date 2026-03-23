package com.shopleft.todo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

import com.shopleft.todo.model.User;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    private String task;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Task() {
        this.createdAt = LocalDate.now();
    }
    public Task(String task) {
        this.task = task;
        this.createdAt = LocalDate.now();
    }

    // getter and setter
    public Long getId() {
        return this.id;
    }

    public void setTask(String task) {this.task = task;}
    public String getTask() {return this.task;}

    public LocalDate getCreatedAt() {return createdAt;}
}
