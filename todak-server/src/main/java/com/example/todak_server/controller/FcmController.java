package com.example.todak_server.controller;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.MemberSetting;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.repository.MemberSettingRepository;
import com.example.todak_server.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final FcmService fcmService;

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
    @PostMapping("/test")
    public String testPush(@RequestParam Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        fcmService.sendEmotionPush(member);
        return "Test push sent to " + member.getNickname();
    }
}
