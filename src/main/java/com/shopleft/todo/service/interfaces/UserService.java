package com.shopleft.todo.service.interfaces;

import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.model.User;

public interface UserService {
    UserCreated createUser(User user);

    UserProfile authenticateUser(UserAuthentication userAuthentication);

    UserProfile getUserProfile(String username);
}
