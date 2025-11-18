package com.example.todak_server.repository;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByMember(Member member);
    List<WorkSchedule> findByDateBetween(LocalDate start, LocalDate end);
    List<WorkSchedule> findByMemberAndDateBetween(Member member, LocalDate start, LocalDate end);
}
