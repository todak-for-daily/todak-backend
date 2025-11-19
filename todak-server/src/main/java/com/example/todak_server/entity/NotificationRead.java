package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class NotificationRead {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Notification notification;

    private Long memberId;

    private boolean isRead;        // 읽음 여부

    private LocalDateTime readAt;  // 읽은 시간
}
