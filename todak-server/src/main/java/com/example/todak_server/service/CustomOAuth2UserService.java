package com.example.todak_server.service;

import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setProvider(provider.toUpperCase());
                    newMember.setProviderId(providerId);
                    newMember.setEmail(email);
                    newMember.setNickname(name);
                    return memberRepository.save(newMember);
                });

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),
                oAuth2User.getAttributes(),
                "sub"
        );
    }
}