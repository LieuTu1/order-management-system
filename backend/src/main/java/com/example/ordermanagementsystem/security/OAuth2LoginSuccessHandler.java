package com.example.ordermanagementsystem.security;

import com.example.ordermanagementsystem.entity.RefreshToken;
import com.example.ordermanagementsystem.entity.Role;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.RoleRepository;
import com.example.ordermanagementsystem.repository.UserRepository;
import com.example.ordermanagementsystem.service.CustomUserDetailsService;
import com.example.ordermanagementsystem.service.JwtService;
import com.example.ordermanagementsystem.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // Frontend callback URL:
    // Docker: http://localhost:3001/oauth2/callback
    // Local:  http://localhost:3000/oauth2/callback
    private final String frontendCallbackUrl;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public OAuth2LoginSuccessHandler(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            CustomUserDetailsService customUserDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;

        this.frontendCallbackUrl =
                System.getenv().getOrDefault(
                        "FRONTEND_CALLBACK_URL",
                        "http://localhost:3000/oauth2/callback"
                );
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // Lấy thông tin user mà Google trả về sau khi đăng nhập thành công
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Lấy các attribute cần thiết từ Google
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Tìm user trong DB dựa trên email Google
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;

        if (existingUser.isPresent()) {

            // Nếu user đã tồn tại thì sử dụng user đó
            user = existingUser.get();

        } else {

            // Nếu chưa tồn tại thì tạo user mới với role mặc định là STAFF
            Role staffRole = roleRepository.findByRole("STAFF")
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Role STAFF không tồn tại"));

            User newUser = new User();

            // Google email được dùng làm username
            newUser.setEmail(email);
            newUser.setUsername(email);

            // Tên Google lưu vào fullName
            newUser.setFullName(name);

            // Google không cung cấp phone trong flow hiện tại
            newUser.setPhone("N/A");

            // Tạo password ngẫu nhiên và mã hóa bằng BCrypt
            newUser.setPassword(
                    passwordEncoder.encode(UUID.randomUUID().toString())
            );

            // User Google mới được kích hoạt ngay
            newUser.setStatus("ACTIVE");

            // Gán role STAFF mặc định
            newUser.setRole(staffRole);

            // Lưu user
            user = userRepository.save(newUser);
        }

        // Chuyển User trong DB thành UserDetails
        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        user.getUsername()
                );

        // Tạo JWT
        String token = jwtService.generateToken(userDetails);

        // Tạo refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        // Redirect về frontend callback
        String redirectUrl = frontendCallbackUrl
                + "?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&refreshToken="
                + URLEncoder.encode(
                refreshToken.getToken(),
                StandardCharsets.UTF_8
        );

        response.sendRedirect(redirectUrl);
    }
}