package com.example.backend.controller.user;

import com.example.backend.dto.user.UserDto;
import com.example.backend.dto.user.UserUpdateRequest;
import com.example.backend.entity.message.Notification;
import com.example.backend.entity.user.User;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public UserController(UserServiceImpl userService, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
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


    @GetMapping("/vip-status")
    public ResponseEntity<Boolean> getVipStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.isVipActive());
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


}
