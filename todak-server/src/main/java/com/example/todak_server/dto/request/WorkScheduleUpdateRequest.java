package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "근무 스케줄 수정 요청 DTO")
public record WorkScheduleUpdateRequest(

        @Schema(description = "근무 날짜", example = "2025-11-20")
        LocalDate date,

        @Schema(description = "시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "18:00")
        LocalTime endTime,

        @Schema(description = "근무 설명", example = "현장 점검 및 장비 세팅")
        String description
) {}
