package com.shopleft.todo.service;

import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final OidcUserService delegate = new OidcUserService();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOidcUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        String subject = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String username = (email != null && !email.isBlank()) ? email : provider + "_" + subject;

        Optional<User> appUser = userRepository.findByOauth2ProviderAndOauth2Subject(provider, subject);
        if (appUser.isEmpty()) {
            appUser = userRepository.findByUsername(username);
        }
        if (appUser.isEmpty()) {
            User user = new User(username, subject, provider, passwordEncoder.encode(UUID.randomUUID().toString()), username);
            userRepository.save(user);
        }
        else {
            User user = appUser.orElseThrow();
            user.setUsername(username);
            user.setOauth2Provider(provider);
            user.setOauth2Subject(subject);
            user.setEmail(email);
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            }
            userRepository.save(user);
        }
        List<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>();
        userAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOidcUser(userAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(),"sub");
    }
}
