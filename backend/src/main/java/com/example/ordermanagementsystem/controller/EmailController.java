package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // tạo API POST để gửi email
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendEmail(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text) {

        emailService.sendEmail(to, subject, text);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Email sent successfully");
        response.setData(null);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
