package com.example.todak_server.dto.auth;

import com.example.todak_server.entity.Member;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final BackendUserPayload user;

    public LoginResponse(String accessToken, String refreshToken, Member member) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = new BackendUserPayload(member);
    }
}