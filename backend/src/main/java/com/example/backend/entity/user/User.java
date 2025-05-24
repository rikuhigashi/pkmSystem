package com.example.backend.entity.user;

import com.example.backend.entity.side.Sidedatum;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

//邮箱验证令牌

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Size(max = 60)
    @NotNull
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Size(max = 100)
    @Column(name = "username", length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    @ColumnDefault("1")
    private Role role;

    @Size(max = 255)
    @Column(name = "email",unique = true,updatable = false)
    private String email;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "email_verified", nullable = false)
    @ColumnDefault("false")
    private Boolean emailVerified = false;



    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private Set<Sidedatum> sidedata = new LinkedHashSet<>();

    @Column
    private boolean vipActive; // VIP字段

    @Column(name = "vip_expire_time")
    private LocalDateTime vipExpireTime;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    // 角色枚举定义
    public enum Role {
        ADMIN,  // 对应数据库中的 0
        USER    // 对应数据库中的 1
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", passwordHash='" + passwordHash + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", emailVerified=" + emailVerified +
                ", sidedata=" + sidedata +
                '}';
    }
}