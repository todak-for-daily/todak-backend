package com.example.todak_server.ai.behavior.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AiActionDetailResponse(
        @Schema(description = "사용자의 선택 행동", example="간단한 명상하기")
        String selectedAction,
        @Schema(description = "선택 행동에 대한 이모지", example = "✊➡️✋🔟")
        String selectedEmojis,
        @Schema(description = "선택 행동에 대한 세부 단계 리스트", example = "")
        List<String> actionSteps
) {}
