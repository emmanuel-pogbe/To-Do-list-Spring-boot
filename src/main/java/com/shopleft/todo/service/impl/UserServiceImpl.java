package com.shopleft.todo.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.exception.custom.UserAlreadyExistsException;
import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.UserRepository;
import com.shopleft.todo.service.interfaces.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public UserCreated createUser(User user) {
        String username = user.getUsername();
        Optional<User> doesExist = userRepository.findByUsername(username);

        UserCreated result = new UserCreated();
        result.setUserName(user.getUsername());

        if (doesExist.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            result.setUserId(savedUser.getId());
            result.setCreateStatus(true);
        } else {
            throw new UserAlreadyExistsException("User already exists");
        }
        return result;
    }

    public UserProfile authenticateUser(UserAuthentication userAuthentication) {
        try {
            Authentication authenticatedUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userAuthentication.getUserName(),
                    userAuthentication.getPassword()
                )
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticatedUser);
            SecurityContextHolder.setContext(securityContext);

            return getUserProfile(userAuthentication.getUserName());
        } catch (AuthenticationException ex) {
            throw new SecurityException("Invalid credentials");
        }
    }

    public UserProfile loginUser(UserAuthentication userAuthentication, HttpServletRequest request) {
        UserProfile profile = authenticateUser(userAuthentication);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            securityContext
        );

        return profile;
    }

    public void logoutUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);

            if (session != null) {
                session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                session.invalidate();
            }
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    public UserProfile getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("User not found"));

        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setUserName(user.getUsername());
        profile.setAuthenticated(true);
        return profile;
    }
}
