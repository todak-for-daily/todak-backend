package com.example.todak_server.controller;

import com.example.todak_server.dto.request.HabitRequest;
import com.example.todak_server.dto.response.HabitResponse;
import com.example.todak_server.entity.Habit;
import com.example.todak_server.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자 행동 특성 API", description = "사용자의 행동 특성 등록/조회/삭제")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    @Operation(summary = "행동 특성 생성", description = "사용자의 행동 특성 등록 (감각/인지 구조)")
    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestBody HabitRequest habitRequest
    ) {
        Habit habit = habitService.createHabit(memberId, habitRequest);
        return ResponseEntity.ok(HabitResponse.from(habit));
    }

    @Operation(summary = "전체 행동 특성 조회", description = "특정 사용자의 모든 행동 특성 리스트 조회")
    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits(
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        List<HabitResponse> habits = habitService.getHabits(memberId)
                .stream()
                .map(HabitResponse::from)
                .toList();

        return ResponseEntity.ok(habits);
    }

    @Operation(summary = "행동 특성 수정", description = "전체 필드(type, senseType 등) 모두 전달해야 합니다.")
    @PutMapping("/{habitId}")
    public ResponseEntity<HabitResponse> updateHabit(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @PathVariable Long habitId,
            @RequestBody HabitRequest habitRequest
    ) {
        Habit updated = habitService.updateHabit(memberId, habitId, habitRequest);
        return ResponseEntity.ok(HabitResponse.from(updated));
    }

    @Operation(summary = "행동 특성 삭제", description = "선택한 행동 특성을 삭제")
    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteHabit(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @PathVariable Long habitId
    ) {
        habitService.deleteHabit(memberId, habitId);
        return ResponseEntity.noContent().build();
    }
}
