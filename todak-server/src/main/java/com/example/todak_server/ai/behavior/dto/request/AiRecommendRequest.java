package com.example.todak_server.ai.behavior.dto.request;

public record AiRecommendRequest(
        Long memberId,
        String situationCardId
) {
}
