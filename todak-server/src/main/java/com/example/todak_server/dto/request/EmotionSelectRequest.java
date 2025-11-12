package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmotionSelectRequest(
        @Schema(description = "사용자가 선택한 감정카드", example = "힘들어요")
        String emotionCard
) {}

