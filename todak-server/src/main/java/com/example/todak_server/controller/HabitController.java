package com.example.todak_server.controller;

import com.example.todak_server.dto.request.HabitRequest;
import com.example.todak_server.dto.response.HabitResponse;
import com.example.todak_server.entity.Habit;
import com.example.todak_server.service.HabitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitController {
    private final HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(@AuthenticationPrincipal(expression = "id") Long memberId, @RequestBody HabitRequest habitRequest) {
        Habit habit = habitService.createHabit(memberId, habitRequest.content());
        System.out.println(memberId);
        return ResponseEntity.ok(HabitResponse.from(habit));
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits( @AuthenticationPrincipal(expression = "id") Long memberId) {
        List<HabitResponse> habits = habitService.getHabits(memberId)
                .stream()
                .map(HabitResponse::from)
                .toList();
        return ResponseEntity.ok(habits);
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteHabit( @AuthenticationPrincipal(expression = "id") Long memberId,@PathVariable Long habitId) {
        habitService.deleteHabit(memberId,habitId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/debug/principal")
    public ResponseEntity<?> debug(@AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(principal.getClass().getName());
    }

}
