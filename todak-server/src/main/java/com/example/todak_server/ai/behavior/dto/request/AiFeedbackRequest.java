package com.example.todak_server.ai.behavior.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "행동 후의 감정")
public record AiFeedbackRequest(
        @Schema(description = "행동 후의 감정", example = "괜찮아요")
        String afterEmotion
) {
}
