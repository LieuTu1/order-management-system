package com.example.ordermanagementsystem.security;

import com.example.ordermanagementsystem.service.CustomUserDetailsService;
import com.example.ordermanagementsystem.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService customUserDetailsService, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy Authorization Header
        String authHeader = request.getHeader("Authorization");

        // 2. Không có JWT -> bỏ qua Filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Cắt lấy JWT
            String jwt = authHeader.substring(7);

            // 4. Lấy username từ JWT
            String username = jwtService.extractUsername(jwt);

            // // Chỉ xử lý tiếp nếu: có username hợp lệ VÀ request này CHƯA được xác thực trước đó
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Load UserDetails từ DB
                UserDetails userDetails =
                        customUserDetailsService.loadUserByUsername(username);

                // 7. Kiểm tra JWT hợp lệ
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 8. Tạo Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 9. Lưu Authentication vào SecurityContext
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

            // 10. Cho request đi tiếp
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid JWT", e));
        }
    }
}