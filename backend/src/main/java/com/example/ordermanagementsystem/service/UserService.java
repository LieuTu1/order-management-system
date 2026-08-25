package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.UserRequest;
import com.example.ordermanagementsystem.dto.response.UserResponse;
import com.example.ordermanagementsystem.entity.EmailVerificationToken;
import com.example.ordermanagementsystem.entity.Role;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.DuplicateResourceException;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.RoleRepository;
import com.example.ordermanagementsystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final EmailService emailService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationTokenService emailVerificationTokenService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.emailService = emailService;
    }

    // Entity -> Response
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAddress(user.getAddress());
        response.setStatus(user.getStatus());

        response.setRoleId(user.getRole().getId());
        response.setRoleName(user.getRole().getRole());

        return response;
    }

    // Create
    public UserResponse createUser(UserRequest request) {

        // Kiểm tra username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username đã tồn tại: " + request.getUsername()
            );
        }

        // Kiểm tra email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email đã tồn tại: " + request.getEmail()
            );
        }

        // 1. Validate Role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role", request.getRoleId()));

        // 2. Mapping Request -> Entity
        User user = new User();
        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setStatus("ACTIVE");
        user.setRole(role);

        // 3. Save
        userRepository.save(user);

        // 4. Tạo verification token
        EmailVerificationToken verificationToken =
                emailVerificationTokenService.createToken(user);

        // 5. Tạo link xác nhận email
        String verificationLink =
                "http://localhost:8080/api/users/verify-email?token="
                        + verificationToken.getToken();

        // 6. Gửi email xác nhận
        emailService.sendEmail(
                user.getEmail(),
                "Verify your email",
                "Xin chào " + user.getFullName()
                        + ",\n\nVui lòng click vào link sau để xác nhận email:\n"
                        + verificationLink
                        + "\n\nLink có hiệu lực trong 24 giờ."
        );

        return mapToResponse(user);
    }

    // Get All
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            responses.add(mapToResponse(user));
        }

        return responses;
    }

    // Get All có phân trang
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::mapToResponse);
    }

    // Get By id
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id));

        return mapToResponse(user);
    }

    // Update
    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id));

        // Chỉ kiểm tra khi username thay đổi
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new DuplicateResourceException(
                    "Username đã tồn tại: " + request.getUsername()
            );
        }

        // Chỉ kiểm tra khi email thay đổi
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email đã tồn tại: " + request.getEmail()
            );
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role", request.getRoleId()));

        user.setUsername(request.getUsername());

        // Chỉ đổi mật khẩu khi ADMIN thực sự nhập gì đó,
        // để trống thì giữ nguyên mật khẩu cũ
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setRole(role);

        return mapToResponse(userRepository.save(user));
    }

    // Delete
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id));

        userRepository.delete(user);
    }

    // Gửi lại email xác nhận cho User đã tồn tại
    public void sendVerificationEmail(Long id) {

        // Tìm User theo ID
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id));

        // Tạo verification token mới
        EmailVerificationToken verificationToken =
                emailVerificationTokenService.createToken(user);

        // Tạo link xác nhận email
        String verificationLink =
                "http://localhost:8080/api/users/verify-email?token="
                        + verificationToken.getToken();

        // Gửi email xác nhận
        emailService.sendEmail(
                user.getEmail(),
                "Verify your email",
                "Xin chào " + user.getFullName()
                        + ",\n\nVui lòng click vào link sau để xác nhận email:\n"
                        + verificationLink
                        + "\n\nLink có hiệu lực trong 24 giờ."
        );
    }

    public void verifyEmail(String token) {
        emailVerificationTokenService.verifyEmail(token);
    }
}