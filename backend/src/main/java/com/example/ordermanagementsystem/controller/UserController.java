package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.UserRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.UserResponse;
import com.example.ordermanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. GET all
    // Lấy danh sách tất cả User (có phân trang)
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<UserResponse> userPage =
                userService.getAllUsers(pageable);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("User retrieved successfully");
        response.setData(userPage);

        log.info("Get all users successfully");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 2. GET by id
    // Lấy thông tin User theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long id) {

        UserResponse userResponse =
                userService.getUserById(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setData(userResponse);

        log.info("Get user successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 3. POST create
    // Tạo User mới
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody UserRequest request) {

        UserResponse userResponse =
                userService.createUser(request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("User created successfully");
        response.setData(userResponse);

        log.info(
                "User created successfully, id={}",
                userResponse.getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. PUT update
    // Cập nhật thông tin User
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        UserResponse userResponse =
                userService.updateUser(id, request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("User updated successfully");
        response.setData(userResponse);

        log.info(
                "User updated successfully, id={}",
                id
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 5. DELETE
    // Xóa User theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id) {

        userService.deleteUser(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("User deleted successfully");
        response.setData(null);

        log.info(
                "User deleted successfully, id={}",
                id
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 6. POST resend verification email
    // Gửi lại email xác nhận cho User đã tồn tại
    @PostMapping("/{id}/send-verification")
    public ResponseEntity<ApiResponse> sendVerificationEmail(
            @PathVariable Long id) {

        userService.sendVerificationEmail(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(
                "Verification email sent successfully"
        );
        response.setData(null);

        log.info(
                "Verification email sent successfully, userId={}",
                id
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 7. GET verify email
    // Xác nhận email bằng verification token
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(
            @RequestParam("token") String token) {

        userService.verifyEmail(token);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(
                "Email verified successfully"
        );
        response.setData(null);

        log.info("Email verified successfully");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}