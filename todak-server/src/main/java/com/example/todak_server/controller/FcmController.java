package com.example.todak_server.controller;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.MemberSetting;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.repository.MemberSettingRepository;
import com.example.todak_server.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FCM API", description = "푸시 알림(Firebase Cloud Messaging) 관련 API")
@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final FcmService fcmService;

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록하고 감정 푸시 스케줄링을 시작함.")
    @PostMapping("/register")
    public String registerToken(@RequestParam Long memberId, @RequestParam String token) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setFcmToken(token);
        memberRepository.save(member);

        MemberSetting setting = memberSettingRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("No setting found for this member"));

        fcmService.scheduleEmotionPush(setting);
        return "Token registered and emotion push scheduled.";
    }

    // postman 확인용 테스트 로직
    @Operation(summary = "푸시 테스트 발송", description = "특정 멤버에게 테스트용 감정 푸시 알림을 보냄")
    @PostMapping("/test")
    public String testPush(@RequestParam Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        fcmService.sendEmotionPush(member);
        return "Test push sent to " + member.getNickname();
    }
}
