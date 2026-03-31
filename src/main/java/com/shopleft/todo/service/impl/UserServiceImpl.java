package com.shopleft.todo.service.impl;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.exception.custom.UserAlreadyExistsException;
import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.UserRepository;
import com.shopleft.todo.service.interfaces.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserCreated createUser(User user) {
        String username = user.getUsername();
        Optional<User> doesExist = userRepository.findByUsername(username);

        UserCreated result = new UserCreated();
        result.setUserName(user.getUsername());

        if (doesExist.isEmpty()) {
            User savedUser = userRepository.save(user);
            result.setUserId(savedUser.getId());
            result.setCreateStatus(true);
        } else {
            throw new UserAlreadyExistsException("User already exists");
        }
        return result;
    }

    public UserProfile authenticateUser(UserAuthentication userAuthentication) {
        Optional<User> userOptional = userRepository.findByUsername(userAuthentication.getUserName());

        UserProfile profile = new UserProfile();

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String password = userAuthentication.getPassword();

            if (password != null && password.equals(user.getPassword())) {
                profile.setUserId(user.getId());
                profile.setUserName(user.getUsername());
                profile.setAuthenticated(true);
                return profile;
            }
            else {
                throw new SecurityException("Invalid credentials");
            }
        }
        else {
            throw new NoSuchElementException("User not found");
        }
    }
}
