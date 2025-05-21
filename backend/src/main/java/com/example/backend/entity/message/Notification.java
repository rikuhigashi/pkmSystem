package com.example.backend.entity.message;

import com.example.backend.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @Column(name = "is_read")
    private boolean isRead = false;

    private Instant createdAt = Instant.now();

    private Instant expireAt;

    @Column(name = "related_data_id")
    private Integer relatedDataId;
}
