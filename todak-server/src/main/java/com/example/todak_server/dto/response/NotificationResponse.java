package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "알림 조회 응답 DTO")
public record NotificationResponse(
        @Schema(description = "알림 ID", example = "10")
        Long id,

        @Schema(description = "알림 제목", example = "근무 환경 점검 안내")
        String title,

        @Schema(description = "알림 내용", example = "오늘 중으로 근무환경 체크해주세요.")
        String body,

        @Schema(description = "공지 분류", example = "스케줄")
        String category,

        @Schema(description = "해당 직원이 읽었는지 여부", example = "false")
        boolean isRead,

        @Schema(description = "생성 시각", example = "2025-11-18T14:22:31")
        LocalDateTime createdAt
) {}
