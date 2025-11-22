package com.example.todak_server.dto.request;

import com.example.todak_server.entity.HabitSenseType;
import com.example.todak_server.entity.HabitType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "행동 특성 생성 요청")
public record HabitRequest(

        @Schema(description = "행동 특성 유형", example = "SENSE", allowableValues = {"SENSE", "COGNITION"})
        HabitType type,

        // 감각(SENSE)일 때 사용 -
        @Schema(description = "감각 유형 (SENSE일 때만 사용)", example = "TOUCH", allowableValues = {
                "VISUAL", "AUDITORY", "TASTE", "SMELL", "TOUCH", "KINESTHETIC"
        })
        HabitSenseType senseType,

        // 인지(COGNITION)일 때 사용
        @Schema(description = "언제 나타나나요? (COGNITION일 때)", example = "아침, 출근 준비할 때")
        String time,

        @Schema(description = "주로 어디에서 일어나나요? (COGNITION일 때)", example = "회사 복도에서")
        String place,

        @Schema(description = "누구와 있을 때 나타나나요? (COGNITION일 때)", example = "여러 사람이 있을 때")
        String target,

        // --- 공통 ---
        @Schema(description = "이 상황에서 어떻게 행동하나요?", example = "미지근한 물로 세수하는 것을 좋아해요.")
        String description,

        @Schema(description = "어떤 상황에서 나타나나요?")
        String trigger,

        @Schema(description = "이 특성으로 인해 불안을 느낀다면, 무엇을 하면 괜찮아지나요?", example = "조용한 곳으로 자리를 옮기면 진정돼요.")
        String soothingAction


) {}
