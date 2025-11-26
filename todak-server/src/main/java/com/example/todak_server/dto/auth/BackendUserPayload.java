package com.example.todak_server.dto.auth;

import com.example.todak_server.entity.Member;
import lombok.Getter;

@Getter
public class BackendUserPayload {
    private final Long id;
    private final String email;
    private final String name;
    private final String avatarUrl;
    private final String role;
    private final String organization;
    private final Long organizationUnitId;

    public BackendUserPayload(Member m) {
        this.id = m.getId();
        this.email = m.getEmail();
        this.name = m.getNickname();
        this.avatarUrl = null;
        this.role = "USER"; // 기본 세팅
        this.organization = null;
        this.organizationUnitId = null;
    }
}
