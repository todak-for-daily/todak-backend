package com.example.todak_server.repository;

import com.example.todak_server.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    List<ChangeLog> findByMemberIdOrderByChangedAtDesc(Long memberId);
}