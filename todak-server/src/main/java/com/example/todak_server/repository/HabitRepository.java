package com.example.todak_server.repository;

import com.example.todak_server.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByMemberId(Long memberId);
}
