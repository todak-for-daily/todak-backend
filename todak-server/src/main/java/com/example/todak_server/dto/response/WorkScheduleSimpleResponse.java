package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "직원의 간단한 근무 일정 요약 응답 DTO")
public record WorkScheduleSimpleResponse(

        @Schema(description = "스케줄 ID", example = "12")
        Long scheduleId,

        @Schema(description = "근무 날짜", example = "2025-11-18")
        LocalDate date,

        @Schema(description = "근무 시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "근무 종료 시간", example = "18:00")
        LocalTime endTime,

        @Schema(description = "근무 이미지 URL", example = "https://storage.googleapis.com/.../image01.jpg")
        String imgUrl
) {}
