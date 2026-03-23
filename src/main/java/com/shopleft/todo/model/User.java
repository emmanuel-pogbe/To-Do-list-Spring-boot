package com.shopleft.todo.model;

import jakarta.persistence.*;

import com.shopleft.todo.model.Task;

import java.util.List;

@Entity
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    @OneToMany(mappedBy = "user")
    private List<Task> tasks;

    // parameterized and non-parameterized constructors
    public User() {}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // getters and setters
    public long getId() {return this.id;}

    public String getUsername() {return this.username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}


}
