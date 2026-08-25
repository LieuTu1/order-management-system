package com.example.ordermanagementsystem.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Logger dùng để ghi log quá trình xử lý JWT
    private static final Logger log =
            LoggerFactory.getLogger(JwtService.class);

    // Tạo SecretKey từ chuỗi secret trong application.properties
    private SecretKey getSignInKey(){
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Tạo JWT token cho user sau khi đăng nhập thành công
    public String generateToken(UserDetails userDetails){

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .issuedAt(new Date())                                              // thời điểm tạo = BÂY GIỜ
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))  // thời điểm hết hạn = bây giờ + thời gian sống
                .signWith(getSignInKey())
                .compact();

        // Ghi log khi tạo token thành công
        log.info("JWT token generated successfully for user={}",
                userDetails.getUsername());

        return token;
    }

    // Giải mã JWT và lấy toàn bộ thông tin (Claims) bên trong token
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // Lấy username từ JWT token
    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }


    // Lấy thời gian hết hạn của JWT token
    private Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }


    // Kiểm tra JWT có hợp lệ và đúng user hay không
    public boolean isTokenValid(String token, UserDetails userDetails){

        boolean valid = extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);

        // Chỉ log kết quả kiểm tra, không log token để tránh lộ thông tin nhạy cảm
        if (valid) {
            log.info("JWT token is valid for user={}",
                    userDetails.getUsername());
        } else {
            log.warn("JWT token is invalid for user={}",
                    userDetails.getUsername());
        }

        return valid;
    }


    // Kiểm tra JWT đã hết hạn hay chưa
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
}