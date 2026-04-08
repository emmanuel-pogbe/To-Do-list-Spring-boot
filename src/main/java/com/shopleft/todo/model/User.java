package com.shopleft.todo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String oauth2Provider;

    private String oauth2Subject;

    private String email;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Task> tasks;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<RefreshTokens> refreshTokens;

    // parameterized and non-parameterized constructors
    public User() {}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String email, String oauth2Subject, String oauth2Provider, String password, String username) {
        this.email = email;
        this.oauth2Subject = oauth2Subject;
        this.oauth2Provider = oauth2Provider;
        this.password = password;
        this.username = username;
    }

    // getters and setters
    public long getId() {return this.id;}

    public String getUsername() {return this.username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}


    public String getOauth2Subject() {
        return oauth2Subject;
    }

    public void setOauth2Subject(String oauth2Subject) {
        this.oauth2Subject = oauth2Subject;
    }

    public String getOauth2Provider() {
        return oauth2Provider;
    }

    public void setOauth2Provider(String oauth2Provider) {
        this.oauth2Provider = oauth2Provider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RefreshTokens> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(List<RefreshTokens> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }
}
