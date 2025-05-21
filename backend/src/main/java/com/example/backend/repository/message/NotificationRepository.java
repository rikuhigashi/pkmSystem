package com.example.backend.repository.message;

import com.example.backend.entity.message.Notification;
import com.example.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    List<Notification> findByExpireAtBefore(Instant expireAt);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}