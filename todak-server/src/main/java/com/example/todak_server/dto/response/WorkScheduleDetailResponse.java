package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "직원의 특정 하루 근무 일정 상세 조회 응답 DTO")
public record WorkScheduleDetailResponse(

        @Schema(description = "스케줄 ID", example = "12")
        Long scheduleId,

        @Schema(description = "직원 ID", example = "3")
        Long memberId,

        @Schema(description = "직원 이름", example = "홍길동")
        String memberName,

        @Schema(description = "근무 날짜", example = "2025-11-18")
        LocalDate date,

        @Schema(description = "근무 시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "근무 종료 시간", example = "18:00")
        LocalTime endTime,

        @Schema(description = "근무 내용 설명", example = "현장 점검 및 장비 정리")
        String description,

        @Schema(description = "근무 인증 이미지 URL", example = "https://storage.googleapis.com/.../image01.jpg")
        String imgUrl,

        @Schema(description = "생성 시각", example = "2025-11-18T09:10:55")
        LocalDateTime createdAt,

        @Schema(description = "수정 시각", example = "2025-11-18T10:35:12")
        LocalDateTime updatedAt
) {}
