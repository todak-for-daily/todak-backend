package com.example.todak_server.repository;

import com.example.todak_server.entity.NotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {

    @Query("""
    SELECT nr
    FROM NotificationRead nr
    JOIN FETCH nr.notification n
    WHERE nr.memberId = :memberId
    ORDER BY n.createdAt DESC
    """)

    List<NotificationRead> findByMemberIdOrderByNotificationCreatedAtDesc(
            @Param("memberId") Long memberId
    );
    List<NotificationRead> findByIsReadFalseAndMemberId(Long memberId);
}
