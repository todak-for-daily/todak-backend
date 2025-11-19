package com.example.todak_server.repository;

import com.example.todak_server.entity.ChangeRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeReadRepository extends JpaRepository<ChangeRead, Long> {
    
    List<ChangeRead> findByMemberId(Long memberId);
    List<ChangeRead> findByMemberIdAndIsReadFalse(Long memberId);
    List<ChangeRead> findByIsReadFalseAndNotifiedFalse();
}