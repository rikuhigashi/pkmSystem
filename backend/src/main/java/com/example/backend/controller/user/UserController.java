package com.example.backend.controller.user;

import com.example.backend.dto.user.UserDto;
import com.example.backend.dto.user.UserUpdateRequest;
import com.example.backend.entity.message.Notification;
import com.example.backend.entity.user.User;
import com.example.backend.mapper.knowledge.KnowledgePurchaseRepository;
import com.example.backend.repository.knowledge.KnowledgeRepository;
import com.example.backend.repository.message.NotificationRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.user.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final KnowledgePurchaseRepository knowledgePurchaseRepository;
    private final KnowledgeRepository knowledgeRepository;

    @Autowired
    public UserController(UserServiceImpl userService, UserRepository userRepository, NotificationRepository notificationRepository, KnowledgePurchaseRepository knowledgePurchaseRepository, KnowledgeRepository knowledgeRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.knowledgePurchaseRepository = knowledgePurchaseRepository;
        this.knowledgeRepository = knowledgeRepository;
    }


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


    /**
     * VIP状态
     */
    @GetMapping("/vip-status")
    public ResponseEntity<Map<String, Object>> getVipStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("active", user.isVipActive());
        response.put("expireDate", user.getVipExpireTime());

        return ResponseEntity.ok(response);
    }


    /**
     * 用户获取通知
     */
    @GetMapping("/notifications")
    public List<Notification> getUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("当前请求用户：{}", userDetails.getUsername());


        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        log.info("数据库用户ID：{}", user.getId());
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * 删除通知
     * @param id 通知ID
     */
    @DeleteMapping("/notifications/{id}")
    @Transactional
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        notificationRepository.deleteByIdAndUser(id, user);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));


        // 确保余额不为null
        BigDecimal balance = user.getBalance();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("balance", balance); // 返回余额
        response.put("vipActive", user.isVipActive());
        response.put("vipExpireDate", user.getVipExpireTime());

        return ResponseEntity.ok(response);
    }

    // 获取用户已购买的知识ID列表
    @GetMapping("/purchased-knowledge-ids")
    public ResponseEntity<List<Long>> getPurchasedKnowledgeIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取用户购买的知识ID
        List<Long> purchasedIds = knowledgePurchaseRepository.findKnowledgeIdsByUserId(user.getId());

        // 获取用户创建的知识ID（作者自动拥有权限）
        List<Long> createdIds = knowledgeRepository.findIdsByAuthorId(user.getId());

        // 合并ID列表
        List<Long> allIds = new ArrayList<>();
        allIds.addAll(purchasedIds);
        allIds.addAll(createdIds);

        return ResponseEntity.ok(allIds);
    }




}
