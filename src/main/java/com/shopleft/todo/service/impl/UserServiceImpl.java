package com.shopleft.todo.service.impl;

import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.UserRepository;
import com.shopleft.todo.service.interfaces.UserService;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserCreated createUser(User user) {
        userRepository.save(user);
        return new UserCreated(user.getId(),user.getUsername());
    }
}
