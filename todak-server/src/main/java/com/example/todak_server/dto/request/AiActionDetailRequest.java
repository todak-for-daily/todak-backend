package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiActionDetailRequest(
        @Schema(description = "사용자가 선택한 행동(하나)", example = "간단한 명상하기")
        String selectedAction
) {
}
