package com.shopleft.todo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;
import com.shopleft.todo.response.SuccessResponse;
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
        System.out.println("[HTTP][USER] POST /user/create username=" + user.getUsername());
        return userService.createUser(user); // check createStatus field
    }    

    @PostMapping("/login")
    public ResponseEntity<UserProfile> getUserInfo(
        @RequestBody UserAuthentication userAuthentication,
        HttpServletResponse response
    ) {
        System.out.println("NORMAL LOGIN FLOW with /login");
        System.out.println(userAuthentication);
        System.out.println("[USER] POST /user/login username=" + userAuthentication.getUserName());
        UserProfile checkAuthentication = userService.loginUser(userAuthentication, response);
        return ResponseEntity.ok(checkAuthentication);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserProfile> refresh(
        @CookieValue(value = "refresh_token", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        System.out.println("Access token expired so frontend get's a new one hopefully");
        System.out.println("[HTTP][USER] POST /user/refresh");
        return ResponseEntity.ok(userService.refreshAccessToken(refreshToken, response));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getCurrentUser(Authentication authentication) {
        System.out.println("[HTTP][USER] GET /user/me principal=" + (authentication == null ? "null" : authentication.getName()));
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logoutUser(
        @CookieValue(value = "refresh_token", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        System.out.println("[HTTP][USER] POST /user/logout");
        userService.logoutUser(refreshToken, response);
        return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
    }
}
