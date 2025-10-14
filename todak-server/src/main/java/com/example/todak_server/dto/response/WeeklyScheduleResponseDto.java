package com.example.todak_server.dto.response;

import java.time.LocalTime;

public record WeeklyScheduleResponseDto(
        Long id,
        String dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        String title,
        String color,
        String location
) {}
