package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record WeeklyScheduleRequestDto(
        @Schema(description = "요일(영문 대문자로)", example = "MONDAY")
        String dayOfWeek,   // "MONDAY", "TUESDAY"
        @Schema(description = "시작 시간", example = "08:00:00")
        LocalTime startTime,
        @Schema(description = "종료 시간", example = "09:00:00")
        LocalTime endTime,
        @Schema(description = "일정 제목", example = "아침 식사")
        String title,
        @Schema(description = "색상 코드", example = "#8E7CEA")
        String color,
        @Schema(description = "장소", example = "집")
        String location
) {}
