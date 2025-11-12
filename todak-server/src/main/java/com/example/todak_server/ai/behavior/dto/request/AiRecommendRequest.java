package com.example.todak_server.ai.behavior.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상황카드 id 받아와 추천 행동 3개 줌")
public record AiRecommendRequest(
        @Schema(description = "상황카드 id", example = "COMM-08")
        String situationCardId
) {
}
