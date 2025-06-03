package com.example.backend.service.tiptap;

public interface TiptapCloudService {
    String generateTiptapToken(String userId);

    String getAppId();
}
