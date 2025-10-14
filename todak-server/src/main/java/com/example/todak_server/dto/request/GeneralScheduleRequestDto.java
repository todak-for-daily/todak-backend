package com.example.todak_server.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record GeneralScheduleRequestDto(
        Long memberId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String title,
        String color,
        String location
) {}
