package com.shopleft.todo.dto;

public class UserProfile {
	private Long userId;
	private String userName;
	private boolean authenticated;

	public UserProfile() {}

	public UserProfile(Long userId, String userName, boolean authenticated) {
		this.userId = userId;
		this.userName = userName;
		this.authenticated = authenticated;
	}

    // getters and setters
	public Long getUserId() {return userId;}
	public void setUserId(Long userId) {this.userId = userId;}

	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}

	public boolean isAuthenticated() {return authenticated;}

	public void setAuthenticated(boolean authenticated) {this.authenticated = authenticated;}
}
