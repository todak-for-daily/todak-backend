package com.example.todak_server.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record WorkScheduleDetailResponse(
        Long scheduleId,
        Long memberId,
        String memberName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description,
        String imgUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
