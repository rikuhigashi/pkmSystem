package com.example.backend.dto.auth;


public record JwtResponse(
        String token,
        Integer id,
        String username,
        com.example.backend.entity.user.User.Role role,
        String email,
        boolean vipActive
) {}



