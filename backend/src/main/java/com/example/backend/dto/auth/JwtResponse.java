package com.example.backend.dto.auth;


public record JwtResponse(
        String token,
        String username,

        com.example.backend.entity.user.User.Role role) {}
//    private String token;
//    private String username;
//
//    public JwtResponse(String token,String username) {
//        this.token = token;
//        this.username = username;
//
//    }

