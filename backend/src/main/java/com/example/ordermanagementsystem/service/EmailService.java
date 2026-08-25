package com.example.ordermanagementsystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Gửi email dạng text đơn giản
    @Async
    public void sendEmail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        // Email người nhận
        message.setTo(to);

        // Tiêu đề email
        message.setSubject(subject);

        // Nội dung email
        message.setText(text);

        // Thực hiện gửi email
        mailSender.send(message);
    }
}
