package com.example.todak_server.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record GeneralScheduleResponseDto(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String title,
        String color
) {}
