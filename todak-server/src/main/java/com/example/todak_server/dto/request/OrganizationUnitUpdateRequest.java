package com.example.todak_server.dto.request;

public record OrganizationUnitUpdateRequest(
        String name,
        Long parentId
) {}
