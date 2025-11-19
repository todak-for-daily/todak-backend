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
public class ChangeLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;          // 직원 ID
    private String category;        // 변경된 요소
    private String fieldName;       // 변경된 필드명
    private String oldValue;
    private String newValue;

    private LocalDateTime changedAt;
}