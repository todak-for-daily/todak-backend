package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record HabitRequest(
        @Schema(description = "행동 특성 상황", example = "불안할 때")
        String situation,

        @Schema(description = "행동 특성 문장", example = "반복적으로 손톱을 물어요.")
        String content
) {
}
