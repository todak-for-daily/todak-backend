package com.example.todak_server.dto.response;

import com.example.todak_server.entity.Habit;

public record HabitResponse(Long id, String content) {
    public static HabitResponse from (Habit habit) {
        return new HabitResponse(habit.getId(), habit.getContent());
    }
}
