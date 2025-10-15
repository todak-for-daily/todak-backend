package com.example.todak_server.dto.response;

import java.util.List;

public record AiActionDetailResponse(
        String selectedAction,
        List<String> actionSteps
) {
}
