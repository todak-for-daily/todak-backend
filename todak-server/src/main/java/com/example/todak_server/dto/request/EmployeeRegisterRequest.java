package com.example.todak_server.dto.request;

import com.example.todak_server.entity.Role;

public record EmployeeRegisterRequest(
        String email,
        String nickname,
        Long organizationUnitId
//        Role role
) {}