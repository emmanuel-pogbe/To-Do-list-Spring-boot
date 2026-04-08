package com.shopleft.todo.service.interfaces;

import com.shopleft.todo.model.User;

public interface RefreshTokenService {
    String issueRefreshToken(User user);

    User validateRefreshToken(String refreshToken);

    String rotateRefreshToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

    void revokeAllUserRefreshTokens(User user);
}
