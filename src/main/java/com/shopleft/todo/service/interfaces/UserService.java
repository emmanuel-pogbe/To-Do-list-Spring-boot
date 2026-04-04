package com.shopleft.todo.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.model.User;

public interface UserService {
    UserCreated createUser(User user);

    UserProfile authenticateUser(UserAuthentication userAuthentication);

    UserProfile loginUser(UserAuthentication userAuthentication, HttpServletRequest request);

    void logoutUser(HttpServletRequest request);

    UserProfile getUserProfile(String username);
}
