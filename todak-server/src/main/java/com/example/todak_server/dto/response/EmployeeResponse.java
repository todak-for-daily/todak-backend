package com.example.todak_server.dto.response;

public record EmployeeResponse(
        Long id,
        String email,
        String nickname,
        String role,
        String organizationName
) {}