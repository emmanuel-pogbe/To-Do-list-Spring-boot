package com.shopleft.todo.config;

import com.shopleft.todo.dto.UserProfile;
import com.shopleft.todo.service.interfaces.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        System.out.println("[AUTH][OAUTH] OAuth2LoginSuccesHandler line 35 success from provider=" + provider);

        String username = (oidcUser.getEmail() != null && !oidcUser.getEmail().isBlank()) ? oidcUser.getEmail():provider + "_" + oidcUser.getSubject();
        System.out.println("[AUTH][OAUTH] Username from OAuth2LoginSuccesHandler line 38=" + username);

        UserProfile profile = userService.completeOAuthLogin(username, response);

        String tokenFragment = "#access_token=" + URLEncoder.encode(profile.getAccessToken(), StandardCharsets.UTF_8)
            + "&oauth=success";
        response.sendRedirect("/" + tokenFragment);
    }
}
