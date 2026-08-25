package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // Tìm refresh token khi client gửi token lên
    Optional<RefreshToken> findByToken(String token);

}