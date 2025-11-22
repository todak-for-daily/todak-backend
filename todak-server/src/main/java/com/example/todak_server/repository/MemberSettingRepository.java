package com.example.todak_server.repository;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.MemberSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long> {

    Optional<MemberSetting> findByMember(Member member);
    Optional<MemberSetting> findByMemberId(Long memberId);
}
