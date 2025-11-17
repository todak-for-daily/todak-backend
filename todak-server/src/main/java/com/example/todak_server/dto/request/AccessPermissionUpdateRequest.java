package com.example.todak_server.dto.request;

public record AccessPermissionUpdateRequest(
        boolean canViewSchedule,
        boolean canViewWarning,
        boolean canViewHealth
) {}