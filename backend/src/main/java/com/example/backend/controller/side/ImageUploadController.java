package com.example.backend.controller.side;

import com.example.backend.config.TencentCosConfig;
import com.example.backend.service.side.CosUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class ImageUploadController {

    private final CosUploadService cosUploadService;
    private final TencentCosConfig config;  // 引入配置类

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        String url = cosUploadService.upload(file);
        return ResponseEntity.ok(Map.of("url", url));
    }


    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteImage(@RequestParam("fileKey") String fileKey) {
        String key = extractFileKey(fileKey);  // 提取文件在腾讯云的路径
        cosUploadService.deleteImage(key);  // 调用服务删除图片
        return ResponseEntity.noContent().build();  // 返回204 No Content
    }

    // 提取腾讯云路径的文件名（key）
    private String extractFileKey(String fileUrl) {
        // 解析 URL 获取文件路径
        String[] parts = fileUrl.split(config.getBucket() + ".cos." + config.getRegion() + ".myqcloud.com/");
        return parts.length > 1 ? parts[1] : parts[0];
    }
}
