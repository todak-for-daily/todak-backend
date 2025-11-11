package com.example.todak_server.dto.response;

import com.example.todak_server.entity.Admin;

public record AdminProfileResponse(
        Long id,
        String email,
        String name,
        String phone,
        String avartarUrl,
        String role
) {
    public static AdminProfileResponse from(Admin admin) {
        return new AdminProfileResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getName(),
                admin.getPhone(),
                admin.getAvatarUrl(),
                admin.getRole()
        );
    }
}
