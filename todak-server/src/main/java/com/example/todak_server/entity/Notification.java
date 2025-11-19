package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    private String category; // 알림 종류

    private LocalDateTime createdAt;
}
