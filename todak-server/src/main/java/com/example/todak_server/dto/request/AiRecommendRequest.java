package com.example.todak_server.dto.request;

public record AiRecommendRequest (
        String emotionCard,
        String situationCardId
) {}
