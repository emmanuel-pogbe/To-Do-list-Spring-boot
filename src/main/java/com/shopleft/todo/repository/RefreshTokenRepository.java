package com.shopleft.todo.repository;

import com.shopleft.todo.model.RefreshTokens;
import com.shopleft.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    Optional<RefreshTokens> findByTokenHashAndRevokedFalse(String tokenHash);

    List<RefreshTokens> findByUserAndRevokedFalse(User user);

    List<RefreshTokens> findByExpiresAtBefore(LocalDateTime now);
}
