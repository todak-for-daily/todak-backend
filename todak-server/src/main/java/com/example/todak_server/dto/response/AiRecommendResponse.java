package com.example.todak_server.dto.response;

import java.util.List;

public record AiRecommendResponse(
        List<String> recommendedActions
) {
}
