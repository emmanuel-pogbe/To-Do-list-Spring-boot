package com.shopleft.todo.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    
    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;
    private static final long ACCESS_TOKEN_EXP = 1000L * 60 * 15; // 15 mins
    private static final long REFRESH_TOKEN_EXP = 1000L * 60 * 60 * 24 * 7; // 7 days

    private SecretKey accessSecretKey;
    private SecretKey refreshSecretKey;

    @PostConstruct
    private void init() {
        accessSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, Map<String, Object> claims) {
        System.out.println("[JWTUtils.java line 38 probably] Generating ACCESS token for username=" + username);
        Map<String, Object> tokenClaims = new HashMap<>(claims);
        tokenClaims.put("tokenType", "ACCESS");
        return createToken(tokenClaims, username, ACCESS_TOKEN_EXP, accessSecretKey);
    }

    public String generateRefreshToken(String username, Map<String, Object> claims) {
        System.out.println(" Generating REFRESH token for username=" + username);
        Map<String, Object> tokenClaims = new HashMap<>(claims);
        tokenClaims.put("tokenType", "REFRESH");
        return createToken(tokenClaims, username, REFRESH_TOKEN_EXP, refreshSecretKey);
    }

    private String createToken(Map<String, Object> claims, String subject, long exp, SecretKey key) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromAccessToken(String token) {
        return getValidatedAccessClaims(token).getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return parseRefreshClaims(token).getSubject();
    }

    public boolean validateAccessToken(String token) {
        try {
            getValidatedAccessClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("[TOKEN][JWT] ACCESS token validation failed: " + e.getClass().getSimpleName());
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseRefreshClaims(token);
            return "REFRESH".equals(claims.get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public Date getRefreshTokenExpiration(String token) {
        return parseRefreshClaims(token).getExpiration();
    }

    public long getAccessTokenExpirySeconds() {
        return ACCESS_TOKEN_EXP / 1000;
    }

    public long getRefreshTokenExpirySeconds() {
        return REFRESH_TOKEN_EXP / 1000;
    }

    private Claims parseAccessClaims(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims parseRefreshClaims(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims getValidatedAccessClaims(String token) {
        Claims claims = parseAccessClaims(token);
        String tokenType = claims.get("tokenType", String.class);
        if (!"ACCESS".equals(tokenType)) {
            throw new SecurityException("Invalid token type for access token");
        }
        return claims;
    }
}
