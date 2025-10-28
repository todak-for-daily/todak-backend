package com.example.todak_server.repository;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    Optional<UserSetting> findByMember(Member member);
}
