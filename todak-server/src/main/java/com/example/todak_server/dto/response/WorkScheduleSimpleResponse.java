package com.example.todak_server.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkScheduleSimpleResponse(
        Long scheduleId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String imgUrl
) {}
