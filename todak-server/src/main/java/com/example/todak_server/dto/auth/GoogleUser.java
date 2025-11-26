package com.example.todak_server.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoogleUser {
    private String sub;       // providerId
    private String email;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private String aud;       // Client ID 확인용
}
