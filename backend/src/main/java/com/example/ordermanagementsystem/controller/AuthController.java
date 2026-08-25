package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.LoginRequest;
import com.example.ordermanagementsystem.dto.request.RefreshTokenRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.LoginResponse;
import com.example.ordermanagementsystem.entity.RefreshToken;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.UserRepository;
import com.example.ordermanagementsystem.service.CustomUserDetailsService;
import com.example.ordermanagementsystem.service.JwtService;
import com.example.ordermanagementsystem.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ordermanagementsystem.dto.request.RegisterRequest;
import com.example.ordermanagementsystem.entity.Role;
import com.example.ordermanagementsystem.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.ordermanagementsystem.dto.response.MeResponse;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.ordermanagementsystem.dto.request.ChangePasswordRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository,
            CustomUserDetailsService customUserDetailsService,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. LOGIN
    // Xác thực tài khoản và tạo JWT + Refresh Token
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest request) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        // Xác thực thông tin đăng nhập
        Authentication authentication =
                authenticationManager.authenticate(token);

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails);

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User không tồn tại"));

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setRefreshToken(refreshToken.getToken());

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Login successful");
        response.setData(loginResponse);

        // Không log password, JWT hoặc refresh token
        log.info("User login successfully");

        return ResponseEntity.ok(response);
    }

    // 2. REFRESH TOKEN
    // Tạo JWT mới từ Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.getValidRefreshToken(
                        request.getRefreshToken());

        User user = refreshToken.getUser();

        UserDetails userDetails =
                customUserDetailsService
                        .loadUserByUsername(user.getUsername());

        String jwt = jwtService.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setRefreshToken(refreshToken.getToken());

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Refresh token thành công");
        response.setData(loginResponse);

        // Không log token
        log.info("Refresh token successfully");

        return ResponseEntity.ok(response);
    }
    // 3. REGISTER
// Tạo tài khoản người dùng mới
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Username đã tồn tại",
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Email đã được sử dụng",
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }

        Role defaultRole = roleRepository.findByRole("STAFF")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role mặc định không tồn tại"));

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhone());
        newUser.setEmail(request.getEmail());
        newUser.setStatus("ACTIVE");
        newUser.setRole(defaultRole);

        userRepository.save(newUser);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Đăng ký thành công",
                null
        );

        log.info("New user registered: {}", newUser.getUsername());

        return ResponseEntity.ok(response);
    }

    // 4. GET ME
    // Lấy thông tin user đang đăng nhập (dựa vào token)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(
            Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User không tồn tại"));

        MeResponse meResponse = new MeResponse();
        meResponse.setId(user.getId());
        meResponse.setUsername(user.getUsername());
        meResponse.setFullName(user.getFullName());
        meResponse.setPhone(user.getPhone());
        meResponse.setEmail(user.getEmail());
        meResponse.setAddress(user.getAddress());
        meResponse.setRole(user.getRole().getRole());

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get current user successfully");
        response.setData(meResponse);

        log.info("Get current user successfully, username={}", username);

        return ResponseEntity.ok(response);
    }

    // 5. CHANGE PASSWORD
    // Đổi mật khẩu cho user đang đăng nhập (yêu cầu nhập đúng mật khẩu cũ)
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User không tồn tại"));

        // Xác thực mật khẩu cũ trước khi cho đổi
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Mật khẩu hiện tại không đúng",
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Đổi mật khẩu thành công");
        response.setData(null);

        log.info("User changed password successfully, username={}", username);

        return ResponseEntity.ok(response);
    }
}