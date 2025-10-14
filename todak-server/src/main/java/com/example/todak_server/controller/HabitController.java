package com.example.todak_server.controller;

import com.example.todak_server.dto.request.HabitRequest;
import com.example.todak_server.dto.response.HabitResponse;
import com.example.todak_server.entity.Habit;
import com.example.todak_server.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitController {
    private final HabitService habitService;

    @PostMapping("/{memberId}")
    public ResponseEntity<HabitResponse> createHabit(@PathVariable Long memberId, @RequestBody HabitRequest habitRequest) {
        Habit habit = habitService.createHabit(memberId, habitRequest.content());
        return ResponseEntity.ok(HabitResponse.from(habit));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<HabitResponse>> getAllHabits(@PathVariable Long memberId) {
        List<HabitResponse> habits = habitService.getHabits(memberId)
                .stream()
                .map(HabitResponse::from)
                .toList();
        return ResponseEntity.ok(habits);
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long habitId) {
        habitService.deleteHabit(habitId);
        return ResponseEntity.noContent().build();
    }
}
