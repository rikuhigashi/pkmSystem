package com.example.backend.service.user;

public interface EmailService {

    void sendVerificationEmail(String to , String token);

    void sendPasswordResetEmail(String to, String code);
}
