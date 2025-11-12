package com.example.todak_server.ai.behavior.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AiRecommendResponse(
        @Schema(description = "받아온 추천 행동 3가지",
                example = "[\"짧은 스트레칭 하기\", \"물 한 잔 마시기\", \"5분 명상하기\"]")
        List<String> recommendedActions
) {
}
