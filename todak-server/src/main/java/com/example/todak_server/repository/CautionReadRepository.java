package com.example.todak_server.repository;

import com.example.todak_server.entity.CautionRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CautionReadRepository extends JpaRepository<CautionRead, Long> {

    Optional<CautionRead> findByMemberIdAndCautionId(Long memberId, Long cautionId);
    List<CautionRead> findByMemberId(Long memberId);
    List<CautionRead> findByIsReadFalseAndNotifiedFalse();
}
