package com.example.todak_server.repository;


import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.OrganizationUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(Long id);
    List<Member> findByOrganizationUnitId(Long organizationUnitId);
    boolean existsByOrganizationUnit(OrganizationUnit organizationUnit);
}
