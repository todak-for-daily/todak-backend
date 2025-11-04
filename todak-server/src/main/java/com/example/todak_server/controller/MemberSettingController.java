package com.example.todak_server.controller;

import com.example.todak_server.dto.MemberSettingRequest;
import com.example.todak_server.dto.MemberSettingResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.service.MemberSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setting")
@RequiredArgsConstructor
public class MemberSettingController {

    private final MemberSettingService userSettingService;

    @GetMapping
    public ResponseEntity<MemberSettingResponse> getSetting(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(userSettingService.getSetting(member));
    }

    @PostMapping
    public ResponseEntity<MemberSettingResponse> updateSetting(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberSettingRequest request
    ) {
        return ResponseEntity.ok(userSettingService.updateSetting(member, request));
    }
}
