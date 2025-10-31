package com.example.todak_server.repository;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule,Long> {

    // 특정 사용자의 모든 일주일 시간표 조회
    List<WeeklySchedule> findByMember(Member member);

    // 특정 사용자의 특정 요일에 해당하는 시간표 조회
    List<WeeklySchedule> findByMemberAndDayOfWeek(Member member, com.example.todak_server.entity.DayOfWeek dayOfWeek);

    Optional<WeeklySchedule> findByIdAndMember(Long id, Member member);

}
