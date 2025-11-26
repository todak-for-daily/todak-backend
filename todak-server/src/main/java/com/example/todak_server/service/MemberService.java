package com.example.todak_server.service;

import com.example.todak_server.dto.auth.GoogleUser;
import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member findOrCreateByGoogle(GoogleUser googleUser) {

        String provider = "GOOGLE";
        String providerId = googleUser.getSub();

        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Member m = new Member();
                    m.setProvider(provider);
                    m.setProviderId(providerId);
                    m.setEmail(googleUser.getEmail());
                    m.setNickname(googleUser.getName());
                    return memberRepository.save(m);
                });
    }
}
