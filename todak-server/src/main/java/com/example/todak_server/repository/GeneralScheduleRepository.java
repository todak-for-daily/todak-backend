package com.example.todak_server.repository;

import com.example.todak_server.entity.GeneralSchedule;
import com.example.todak_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GeneralScheduleRepository extends JpaRepository<GeneralSchedule,Long> {

    // 특정 사용자의 일정 전체 조회
    List<GeneralSchedule> findByMember(Member member);

    // 특정 사용자의 일정 중 특정 기간 조회 (ex. 먼슬리 캘린더 ..)
    List<GeneralSchedule> findByMemberAndDateBetween(Member member, LocalDate start, LocalDate end);

    // sourceId로 연결된 general 일정 찾기 (일주일 시간표로 생성된 일정들 찾기.)
    List<GeneralSchedule> findBySourceId(Long sourceId);

    Optional<GeneralSchedule> findByIdAndMember(Long id, Member member);

}
