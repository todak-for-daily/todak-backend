package com.example.todak_server.repository;

import com.example.todak_server.entity.Event;
import com.example.todak_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByMember(Member member);

    List<Event> findByMemberAndStartTimeBetween(
            Member member,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<Event> findByMemberAndStartTimeAfter(Member member, LocalDateTime after);

    List<Event> findByMemberAndStartTimeBefore(Member member, LocalDateTime before);
}
