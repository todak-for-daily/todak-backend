package com.example.todak_server.dto.request;

public record AdminProfileRequest(
        String email,
        String name,
        String phone,
        String avatarUrl,
        String role
) {}
