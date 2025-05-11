package com.example.backend.repository.user;

import com.example.backend.entity.user.PasswordResetToken;
import com.example.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {


    Optional<PasswordResetToken> findByToken(String token);


    void deleteByUserEmail(String email);


    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    void deleteByUser(User user);

}