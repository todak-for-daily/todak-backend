package com.example.todak_server.controller;

import com.example.todak_server.dto.MemberSettingRequest;
import com.example.todak_server.dto.MemberSettingResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.service.MemberSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 설정 API", description = "회원 개인 설정(푸시 시간, 푸시 종류 등) 관리 API")
@RestController
@RequestMapping("/setting")
@RequiredArgsConstructor
public class MemberSettingController {

    private final MemberSettingService userSettingService;

    @Operation(summary = "회원 설정 조회", description = "로그인한 회원의 설정 정보를 조회함.")
    @GetMapping
    public ResponseEntity<MemberSettingResponse> getSetting(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(userSettingService.getSetting(member));
    }

    @Operation(summary = "회원 설정 업데이트", description = "로그인한 회원의 설정 정보를 수정함.")
    @PostMapping
    public ResponseEntity<MemberSettingResponse> updateSetting(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberSettingRequest request
    ) {
        return ResponseEntity.ok(userSettingService.updateSetting(member, request));
    }
}
