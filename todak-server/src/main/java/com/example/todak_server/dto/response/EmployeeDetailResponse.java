package com.example.todak_server.dto.response;

public record EmployeeDetailResponse(
        Long id,
        String email,
        String nickname,
        String role,
        String organizationName,
        boolean canViewSchedule,
        boolean canViewWarning,
        boolean canViewHealth
) {}
