package com.example.todak_server.dto.response;

import com.example.todak_server.entity.Habit;
import com.example.todak_server.entity.HabitSenseType;
import com.example.todak_server.entity.HabitType;
import io.swagger.v3.oas.annotations.media.Schema;

public record HabitResponse(
        @Schema(description = "행동 특성 id", example = "1")
        Long id,

        @Schema(description = "행동 특성 유형", example = "SENSE", allowableValues = {"SENSE", "COGNITION"})
        HabitType type,

        @Schema(description = "감각 유형 (SENSE일 때만 의미 있음)", example = "TOUCH")
        HabitSenseType senseType,

        @Schema(description = "언제 나타나나요? (COGNITION일 때)", example = "아침, 출근 준비할 때")
        String time,

        @Schema(description = "어디에서 일어나나요? (COGNITION일 때)", example = "회사 복도에서")
        String place,

        @Schema(description = "누구와 있을 때 나타나나요? (COGNITION일 때)", example = "여러 사람이 있을 때")
        String target,

        @Schema(description = "특성 설명", example = "미지근한 물로 세수하는 것을 좋아해요.")
        String description,

        @Schema(description = "힘들 때 스스로 안정되는 방법", example = "조용한 곳으로 자리를 옮기면 진정돼요.")
        String soothingAction
) {
    public static HabitResponse from(Habit habit) {
        return new HabitResponse(
                habit.getId(),
                habit.getType(),
                habit.getSenseType(),
                habit.getTime(),
                habit.getPlace(),
                habit.getTarget(),
                habit.getDescription(),
                habit.getSoothingAction()
        );
    }

    @Override
    public Long id() {
        return id;
    }
}
