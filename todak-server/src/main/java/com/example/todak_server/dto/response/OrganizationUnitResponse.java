package com.example.todak_server.dto.response;

import java.util.List;

public record OrganizationUnitResponse(
        Long id,
        String name,
        List<OrganizationUnitResponse> children
) {}
