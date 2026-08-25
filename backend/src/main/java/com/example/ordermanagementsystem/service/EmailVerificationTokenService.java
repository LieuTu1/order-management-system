package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.entity.EmailVerificationToken;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.EmailVerificationTokenRepository;
import com.example.ordermanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class EmailVerificationTokenService {
    private final EmailVerificationTokenRepository repository;
    private final UserRepository userRepository;

    public EmailVerificationTokenService(EmailVerificationTokenRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public EmailVerificationToken createToken(User user) {
        // tạo token mới
       EmailVerificationToken emailVerificationToken = new EmailVerificationToken();

        // tạo chuỗi token ngẫu nhiên bằng UUID
        emailVerificationToken.setToken(UUID.randomUUID().toString());

        // gán User
        emailVerificationToken.setUser(user);

        // đặt thời gian hết hạn
        emailVerificationToken.setExpiryDate(Instant.now().plusSeconds(86400));

        return repository.save(emailVerificationToken);

    }

    public User verifyEmail(String token) {
        // 1. Tìm verification token trong database
        EmailVerificationToken verificationToken = repository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Verification token không tồn tại"));

        // 2. Kiểm tra token đã hết hạn chưa
        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Verification token đã hết hạn");
        }
        // 3. Lấy User từ token
        User user = verificationToken.getUser();
        // 4. Đổi trạng thái User thành VERIFIED
        user.setStatus("VERIFIED");
        userRepository.save(user);
        // 5. Xóa token đã sử dụng
        repository.delete(verificationToken);
        // 6. Trả về User
        return user;
    }
}