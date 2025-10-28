package com.example.todak_server.repository;

import com.example.todak_server.entity.Habit;
import com.example.todak_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByMember(Member member);

    Optional<Habit> findByIdAndMember(Long id, Member member);
}
