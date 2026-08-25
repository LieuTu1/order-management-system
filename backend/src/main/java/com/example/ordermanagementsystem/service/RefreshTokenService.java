package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.entity.RefreshToken;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.InvalidRefreshTokenException;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token đã bị thu hồi"
            );
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException(
                    "Refresh token đã hết hạn"
            );
        }

        return refreshToken;
    }

    public void revokeToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token không tồn tại"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getValidRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh token không tồn tại"));

        return verifyExpiration(refreshToken);
    }

}
