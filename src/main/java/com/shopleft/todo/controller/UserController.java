package com.shopleft.todo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;
import com.shopleft.todo.service.interfaces.UserService;
import com.shopleft.todo.dto.UserProfile;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public UserCreated createUser(@RequestBody User user) {
        return userService.createUser(user); // check createStatus field
    }    

    @PostMapping("/login")
    public ResponseEntity<UserProfile> getUserInfo(
        @RequestBody UserAuthentication userAuthentication,
        HttpServletRequest request
    ) {
        UserProfile checkAuthentication = userService.authenticateUser(userAuthentication);

        if (checkAuthentication.isAuthenticated()) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
            );
            return ResponseEntity.ok(checkAuthentication);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(checkAuthentication);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }
}
