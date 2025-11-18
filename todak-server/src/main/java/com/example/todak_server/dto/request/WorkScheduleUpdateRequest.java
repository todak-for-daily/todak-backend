package com.example.todak_server.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkScheduleUpdateRequest(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description
) {}
