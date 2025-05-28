package com.example.backend.service.side;

import org.springframework.web.multipart.MultipartFile;

public interface CosUploadService {
    String upload(MultipartFile file);

    void deleteImage(String key);
}
