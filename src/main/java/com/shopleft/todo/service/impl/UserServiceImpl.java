package com.shopleft.todo.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.exception.custom.UserAlreadyExistsException;
import com.shopleft.todo.dto.UserAuthentication;
import com.shopleft.todo.dto.UserCreated;
import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.UserRepository;
import com.shopleft.todo.service.interfaces.RefreshTokenService;
import com.shopleft.todo.service.interfaces.UserService;
import com.shopleft.todo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh.cookie-secure:false}")
    private boolean secureRefreshCookie;

    @Value("${jwt.refresh.cookie-same-site:Lax}")
    private String sameSite;

    public UserServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtUtils jwtUtils,
        RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    public UserCreated createUser(User user) {
        String username = user.getUsername();
        System.out.println("Attempting account creation for username (usual username and password)=" + username);
        Optional<User> doesExist = userRepository.findByUsername(username);

        UserCreated result = new UserCreated();
        result.setUserName(user.getUsername());

        if (doesExist.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            System.out.println("Account created. userId=" + savedUser.getId());
            result.setUserId(savedUser.getId());
            result.setCreateStatus(true);
        } else {
            throw new UserAlreadyExistsException("User already exists");
        }
        return result;
    }

    private User authenticateUser(UserAuthentication userAuthentication) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userAuthentication.getUserName(),
                    userAuthentication.getPassword()
                )
            );
            return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        } catch (AuthenticationException ex) {
            throw new SecurityException("Invalid credentials");
        }
    }

    public UserProfile loginUser(UserAuthentication userAuthentication, HttpServletResponse response) {
        User user = authenticateUser(userAuthentication);
        return issueTokensForUser(user, response);
    }

    public UserProfile completeOAuthLogin(String username, HttpServletResponse response) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        System.out.println("What's a servlet response: UserService Impl line 101");
        System.out.println(response);
        return issueTokensForUser(user, response);
    }

    public UserProfile refreshAccessToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            System.out.println("You didn't put the refresh token :(");
            throw new SecurityException("Refresh token is required");
        }

        System.out.println("[TOKEN][REFRESH] Refresh requested");
        User user = refreshTokenService.validateRefreshToken(refreshToken);
        String rotatedRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
        addRefreshTokenCookie(response, rotatedRefreshToken);

        UserProfile profile = getUserProfile(user.getUsername());
        String accessToken = jwtUtils.generateAccessToken(
            user.getUsername(),
            Map.of("userId", user.getId())
        );
        profile.setAccessToken(accessToken);
        profile.setTokenType("Bearer");
        profile.setExpiresIn(jwtUtils.getAccessTokenExpirySeconds());
        return profile;
    }

    public void logoutUser(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revokeRefreshToken(refreshToken);
            System.out.println("LOGOUT] Refresh token revoked");
        }

        clearRefreshTokenCookie(response);
        System.out.println("LOGOUT Refresh token cookie cleared");
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

    private UserProfile issueTokensForUser(User user, HttpServletResponse response) {
        System.out.println("Issuing access and refresh tokens for userId=" + user.getId());
        UserProfile profile = getUserProfile(user.getUsername());
        String accessToken = jwtUtils.generateAccessToken(
            user.getUsername(),
            Map.of("userId", user.getId())
        );
        String refreshToken = refreshTokenService.issueRefreshToken(user);

        addRefreshTokenCookie(response, refreshToken);

        profile.setAccessToken(accessToken);
        profile.setTokenType("Bearer");
        profile.setExpiresIn(jwtUtils.getAccessTokenExpirySeconds());
        return profile;
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
            .httpOnly(true)
            .secure(secureRefreshCookie)
            .path("/")
            .sameSite(sameSite)
            .maxAge(jwtUtils.getRefreshTokenExpirySeconds())
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(secureRefreshCookie)
            .path("/")
            .sameSite(sameSite)
            .maxAge(0)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
