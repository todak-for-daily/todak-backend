package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record GeneralScheduleRequestDto(
        @Schema(description = "일정 날짜", example = "2025-12-01")
        LocalDate date,
        @Schema(description = "시작 시간", example = "09:00:00")
        LocalTime startTime,
        @Schema(description = "종료 시간", example = "10:30:00")
        LocalTime endTime,
        @Schema(description = "일정 제목", example = "회의")
        String title,
        @Schema(description = "색상 코드", example = "#FFB6C1")
        String color,
        @Schema(description = "장소", example = "회의실 A")
        String location
) {}
