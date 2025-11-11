package com.example.todak_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth/login")
public class AuthLoginController {

    @Operation(summary = "로그인 창", description = "로그인 창으로 이동 후 로그인 완료 처리까지")
    @GetMapping("/google")
    public ResponseEntity<Void> start(HttpServletRequest req) {
        String url = ServletUriComponentsBuilder.fromRequest(req)
                .replacePath("/oauth2/authorization/google")
                .replaceQuery(null)
                .build().toUriString();
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }
}

