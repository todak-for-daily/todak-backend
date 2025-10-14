package com.example.todak_server.repository;


import com.example.todak_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(Long id);
}
