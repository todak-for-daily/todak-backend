package com.example.todak_server.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {
    private String idToken;
}