package com.example.todak_server.controller;

import com.example.todak_server.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token")
    public Map<String, String> generateToken(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        String token = jwtTokenProvider.createToken(email);

        return Map.of("token", token);
    }
}
