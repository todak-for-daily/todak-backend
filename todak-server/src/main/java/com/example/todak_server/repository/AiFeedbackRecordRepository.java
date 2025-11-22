package com.example.todak_server.repository;

import com.example.todak_server.entity.AiFeedbackRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AiFeedbackRecordRepository extends JpaRepository<AiFeedbackRecord,Long> {

    List<AiFeedbackRecord> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<AiFeedbackRecord> findByMemberIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long memberId,
            LocalDateTime start,
            LocalDateTime end
    );
}
