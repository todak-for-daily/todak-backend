package com.example.todak_server.ai.behavior.dto.response;

import java.util.List;

public record AiActionDetailResponse(
        String selectedAction,
        List<String> actionSteps
) {}
