package com.example.backend.service.impl.tiptap;

import com.example.backend.service.tiptap.TiptapCloudService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class TiptapCloudServiceImpl implements TiptapCloudService {

    @Value("${tiptap.cloud.appId}")
    private String appId;

    @Value("${tiptap.cloud.appSecret}")
    private String appSecret;

    @Value("${tiptap.cloud.expiration}")
    private int expiration;


    @Override
    public String generateTiptapToken(String userId) {
        // 创建签名密钥
        Key signingKey = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        // 计算过期时间
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expiration * 1000L;
        Date expDate = new Date(expMillis);

        // 构建 JWT
        return Jwts.builder()
                .claim("appId", appId)
                .claim("userId", userId)
                .issuedAt(new Date(nowMillis))
                .expiration(expDate)
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String getAppId() {
        return appId;
    }
}
