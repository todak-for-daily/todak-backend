package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "근무 스케줄 생성 요청 DTO")
public record WorkScheduleCreateRequest(

        @Schema(description = "근무 대상 직원 ID", example = "3")
        Long memberId,

        @Schema(description = "근무 날짜", example = "2025-11-21")
        LocalDate date,

        @Schema(description = "시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "18:00")
        LocalTime endTime,

        @Schema(description = "근무 설명", example = "오전 회의 및 팀 업무 정리")
        String description
) {}
