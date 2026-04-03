package com.shopleft.todo.config;

import com.shopleft.todo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;

import java.time.OffsetDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
            .map(user -> User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AccessDeniedHandler jsonAccessDeniedHandler = (request, response, ex) -> {
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(buildErrorJson(
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                403,
                request.getRequestURI()
            ));
        };

        http.csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, ex) -> {
                    response.setStatus(401);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(buildErrorJson(
                        "AUTHENTICATION_REQUIRED",
                        "Authentication is required to access this resource",
                        401,
                        request.getRequestURI()
                    ));
                })
                .accessDeniedHandler(jsonAccessDeniedHandler))
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/script.js",
                    "/favicon.ico",
                    "/error",
                    "/user/create",
                    "/user/login",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    private String buildErrorJson(String errorCode, String message, int status, String path) {
        String escapedMessage = message.replace("\"", "\\\"");
        String escapedPath = path.replace("\"", "\\\"");

        return String.format(
            "{\"message\":\"%s\",\"status\":%d,\"errorCode\":\"%s\",\"path\":\"%s\",\"timestamp\":\"%s\"}",
            escapedMessage,
            status,
            errorCode,
            escapedPath,
            OffsetDateTime.now()
        );
    }
}
