package com.example.backend.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "email_verification_token")
public class EmailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true, nullable = false)
//    private String token;

    @Column(nullable = false)
    private String code; // 存储6位数字验证码


    @Column(name = "email", nullable = false)
    private String email;


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt; // 创建时间

    @Column(nullable = false)
    private Instant expirationTime;// 过期时间


}
