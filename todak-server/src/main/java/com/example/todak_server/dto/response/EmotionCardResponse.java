package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmotionCardResponse (
        @Schema(description = "감정카드 id", example = "E001")
        String id,
        @Schema(description = "감정카드 내용", example = "힘들어요")
        String text
) {}
