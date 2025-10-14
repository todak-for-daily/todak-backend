package com.example.todak_server.dto.request;

import java.time.LocalTime;

public record WeeklyScheduleRequestDto(
        Long memberId,
        String dayOfWeek,   // "MONDAY", "TUESDAY"
        LocalTime startTime,
        LocalTime endTime,
        String title,
        String color,
        String location
) {}
