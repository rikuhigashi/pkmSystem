package com.example.backend.controller.user;

import com.example.backend.annotation.CurrentUser;
import com.example.backend.dto.user.UserDto;
import com.example.backend.dto.user.UserUpdateRequest;
import com.example.backend.entity.message.Notification;
import com.example.backend.entity.user.User;
import com.example.backend.mapper.knowledge.KnowledgePurchaseRepository;
import com.example.backend.repository.knowledge.KnowledgeRepository;
import com.example.backend.repository.message.NotificationRepository;
import com.example.backend.service.impl.user.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
    private final NotificationRepository notificationRepository;
    private final KnowledgePurchaseRepository knowledgePurchaseRepository;
    private final KnowledgeRepository knowledgeRepository;


    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserDto updatedUser = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/vip-status")
    public ResponseEntity<Map<String, Object>> getVipStatus(@CurrentUser User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("active", user.isVipActive());
        response.put("expireDate", user.getVipExpireTime());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notifications")
    public List<Notification> getUserNotifications(@CurrentUser User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @DeleteMapping("/notifications/{id}")
    @Transactional
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer id,
            @CurrentUser User user) {
        notificationRepository.deleteByIdAndUser(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@CurrentUser User user) {
        BigDecimal balance = user.getBalance() != null ?
                user.getBalance() : BigDecimal.ZERO;

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("balance", balance);
        response.put("vipActive", user.isVipActive());
        response.put("vipExpireDate", user.getVipExpireTime());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/purchased-knowledge-ids")
    public ResponseEntity<List<Long>> getPurchasedKnowledgeIds(@CurrentUser User user) {
        List<Long> purchasedIds = knowledgePurchaseRepository.findKnowledgeIdsByUserId(user.getId());
        List<Long> createdIds = knowledgeRepository.findIdsByAuthorId(user.getId());

        List<Long> allIds = new ArrayList<>();
        allIds.addAll(purchasedIds);
        allIds.addAll(createdIds);

        return ResponseEntity.ok(allIds);
    }
}
