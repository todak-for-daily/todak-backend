package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CautionRead {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caution_id")
    private Caution caution;

    private boolean isRead;

    private boolean notified; // 알림 발송 여부

    private LocalDateTime readAt;

    @PrePersist
    public void prePersist() {
        this.isRead = false;
        this.notified = false;
    }
}
