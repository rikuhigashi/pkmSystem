package com.example.backend.controller.tiptap;

import com.example.backend.service.impl.tiptap.TiptapCloudServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tiptap")
@RequiredArgsConstructor
public class TiptapController {

    private final TiptapCloudServiceImpl tiptapCloudService;

    @PostMapping("/generate-token")
    public ResponseEntity<?> generateTiptapToken(Authentication authentication) {
        // 获取当前认证用户ID
        String userId = authentication.getName();

        // 生成 Tiptap Cloud 令牌
        String token = tiptapCloudService.generateTiptapToken(userId);

        // 返回响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("appId", tiptapCloudService.getAppId());

        return ResponseEntity.ok(response);
    }
}
