package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    private Integer eventAlarmMinutes = 30;        // 일정 알림 주기
    private Integer emotionIntervalMinutes = 120;  // 감정 푸시 주기

    private LocalTime emotionActiveStart = LocalTime.of(9, 0);
    private LocalTime emotionActiveEnd = LocalTime.of(21, 0);
}
