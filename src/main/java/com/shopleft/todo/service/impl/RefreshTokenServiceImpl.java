package com.shopleft.todo.service.impl;

import com.shopleft.todo.model.RefreshTokens;
import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.RefreshTokenRepository;
import com.shopleft.todo.service.interfaces.RefreshTokenService;
import com.shopleft.todo.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtUtils jwtUtils) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public String issueRefreshToken(User user) {
        System.out.println("[TOKEN][RefreshTokenServiceImpl] Generating refresh token for userId=" + user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername(), Map.of("userId", user.getId()));
        RefreshTokens token = new RefreshTokens(
            hashToken(refreshToken),
            LocalDateTime.now(),
            LocalDateTime.ofInstant(jwtUtils.getRefreshTokenExpiration(refreshToken).toInstant(), ZoneId.systemDefault()),
            false,
            user
        );
        refreshTokenRepository.save(token);
        return refreshToken;
    }

    @Override
    public User validateRefreshToken(String refreshToken) {
        System.out.println("[TOKEN][REFRESH] Validating refresh token");
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            System.out.println("JWT validation failed, probably need to login agian");
            throw new SecurityException("Invalid refresh token");
        }

        RefreshTokens storedToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(refreshToken))
            .orElseThrow(() -> new SecurityException("Refresh token revoked or unknown"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            System.out.println("Check if the refresh token hasn't expired. refreshId=" + storedToken.getId());
            throw new SecurityException("Refresh token expired");
        }

        System.out.println("[TOKEN][REFRESH] Refresh token validated for userId=" + storedToken.getUser().getId());
        return storedToken.getUser();
    }

    @Override
    @Transactional
    public String rotateRefreshToken(String refreshToken) {
        System.out.println("[TOKEN][REFRESH] Rotating refresh token");
        User user = validateRefreshToken(refreshToken);
        revokeRefreshToken(refreshToken);
        return issueRefreshToken(user);
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        System.out.println("[TOKEN][REFRESH] Revoking refresh token");
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(refreshToken))
            .ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
    }

    @Override
    @Transactional
    public void revokeAllUserRefreshTokens(User user) {
        List<RefreshTokens> activeTokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
        for (RefreshTokens token : activeTokens) {
            token.setRevoked(true);
        }
        refreshTokenRepository.saveAll(activeTokens);
    }

    private String hashToken(String token) {
        // bcrypt here?
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] tokenHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(tokenHash);
        } catch (NoSuchAlgorithmException ex) {
            throw new NoSuchElementException("Unable to hash token");
        }
    }
}
