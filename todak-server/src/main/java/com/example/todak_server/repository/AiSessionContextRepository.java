package com.example.todak_server.repository;

import com.example.todak_server.entity.AiSessionContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiSessionContextRepository extends JpaRepository<AiSessionContext,Long> {
    Optional<AiSessionContext> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
}
