package com.example.todak_server.service;

import com.example.todak_server.dto.auth.GoogleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleUser verify(String idToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

        GoogleUser googleUser = restTemplate.getForObject(url, GoogleUser.class);

        if (googleUser == null || googleUser.getSub() == null) {
            throw new RuntimeException("Invalid Google ID Token");
        }

        return googleUser;
    }
}