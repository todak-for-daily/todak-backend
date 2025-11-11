package com.example.todak_server.service;

import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final MemberRepository memberRepository;

    @Transactional
    public String updateAvatarUrl(Long memberId, String imageUrl) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("member not found"));
        m.setAvatarUrl(imageUrl);
        return m.getAvatarUrl();
    }

    @Transactional(readOnly = true)
    public String getAvatarUrl(Long memberId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("member not found"));
        return m.getAvatarUrl();
    }
}
