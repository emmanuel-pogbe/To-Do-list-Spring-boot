package com.shopleft.todo.service.interfaces;

import jakarta.servlet.http.HttpServletResponse;
import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.model.User;

public interface UserService {
    UserCreated createUser(User user);

    UserProfile loginUser(UserAuthentication userAuthentication, HttpServletResponse response);

    UserProfile refreshAccessToken(String refreshToken, HttpServletResponse response);

    void logoutUser(String refreshToken, HttpServletResponse response);

    UserProfile completeOAuthLogin(String username, HttpServletResponse response);

    UserProfile getUserProfile(String username);
}
