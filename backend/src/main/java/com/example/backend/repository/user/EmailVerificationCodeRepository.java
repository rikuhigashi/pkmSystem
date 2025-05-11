package com.example.backend.repository.user;

import com.example.backend.entity.user.EmailVerificationCode;
import com.example.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    Optional<EmailVerificationCode> findByUser(User user);


    Optional<EmailVerificationCode> findByEmail(String email);

    // 获取最新未过期的验证码
    @Query("SELECT c FROM EmailVerificationCode c WHERE c.email = :email ORDER BY c.expirationTime DESC")
    List<EmailVerificationCode> findLatestByEmail(@Param("email") String email);


    void deleteByExpirationTimeBefore(Instant expirationTime);

    void deleteByEmail(String email);
}