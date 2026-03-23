package com.shopleft.todo.service.interfaces;

import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;

public interface UserService {
    UserCreated createUser(User user);
}
