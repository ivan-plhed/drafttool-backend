package com.ivaplahed.drafttool.security.service;

import com.ivaplahed.drafttool.security.error.SecurityException;
import com.ivaplahed.drafttool.security.repository.RefreshTokenRepository;
import com.ivaplahed.drafttool.repository.UserRepository;
import com.ivaplahed.drafttool.security.repository.model.RefreshToken;
import com.ivaplahed.drafttool.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.token.expiration.millis}")
    private long refreshTokenExpirationMillis;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(UUID.fromString(token));
    }

    @Transactional
    public RefreshToken generateRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        existingToken.ifPresent(refreshTokenRepository::delete);

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMillis));
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new SecurityException("Refresh token was expired. Please make a new login request");
        }

        return token;
    }

}