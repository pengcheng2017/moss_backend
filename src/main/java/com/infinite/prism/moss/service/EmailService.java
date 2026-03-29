package com.infinite.prism.moss.service;

public interface EmailService {
    void sendVerificationCode(String toEmail, String username, String code);
    void sendSimpleEmail(String toEmail, String subject, String content);
}