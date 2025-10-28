package com.example.todak_server.service;

import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User delegate = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase(); // "GOOGLE"
        String providerId = delegate.getAttribute("sub"); // 구글 고유 Id
        String email      = delegate.getAttribute("email"); // null가능
        String name       = delegate.getAttribute("name"); // null 가능

        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .map(m -> {
                    // (선택) 변경분 반영
                    if (email != null && !email.equals(m.getEmail()))    m.setEmail(email);
                    if (name  != null && !name.equals(m.getNickname()))  m.setNickname(name);
                    return m;
                })
                .orElseGet(() -> {
                    Member m = new Member();
                    m.setProvider(provider);
                    m.setProviderId(providerId); //식별자 = providerId(sub)
                    m.setEmail(email);
                    m.setNickname(name);
                    return memberRepository.save(m);
                });

        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                delegate.getAttributes(),
                "sub"
        );
    }
}
