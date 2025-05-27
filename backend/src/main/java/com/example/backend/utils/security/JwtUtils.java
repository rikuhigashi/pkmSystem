package com.example.backend.utils.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds


    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // 初始化 key
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateJwtToken(Authentication authentication) {

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {

        return parseToken(token).getSubject();



    }


    public boolean validateToken(String token) {

        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }

//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException e) {
//            log.error("Token 已过期: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            log.error("Token 格式不支持: {}", e.getMessage());
//        } catch (MalformedJwtException e) {
//            log.error("Token 格式错误: {}", e.getMessage());
//        } catch (Exception e) {
//            log.error("Token 验证失败: {}", e.getMessage());
//        }
//        return false;
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).getSubject(); // 邮箱为subject

    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key) // 统一使用 verifyWith
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
