package com.example.todak_server.dto.response;

import com.example.todak_server.entity.Habit;
import io.swagger.v3.oas.annotations.media.Schema;

public record HabitResponse(
        @Schema(description = "행동특성id", example = "1")
        Long id,
        @Schema(description = "행동 특성 상황", example = "불안할 때")
        String situation,
        @Schema(description = "행동 특성 내용", example = "반복적으로 손톱을 물어요.")
        String content
) {
    public static HabitResponse from (Habit habit) {
        return new HabitResponse(habit.getId(), habit.getSituation(), habit.getContent());
    }
}
