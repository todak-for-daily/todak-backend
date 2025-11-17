package com.example.todak_server.dto.request;

public record OrganizationUnitCreateRequest(
        String name,
        Long parentId
) {}
