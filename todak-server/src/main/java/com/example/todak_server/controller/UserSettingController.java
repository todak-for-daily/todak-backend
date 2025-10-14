package com.example.todak_server.controller;

import com.example.todak_server.dto.UserSettingRequest;
import com.example.todak_server.dto.UserSettingResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingService userSettingService;

    @GetMapping
    public ResponseEntity<UserSettingResponse> getSetting(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(userSettingService.getSetting(member));
    }

    @PostMapping
    public ResponseEntity<UserSettingResponse> updateSetting(
            @AuthenticationPrincipal Member member,
            @RequestBody UserSettingRequest request
    ) {
        return ResponseEntity.ok(userSettingService.updateSetting(member, request));
    }
}
