package com.example.todak_server.dto.request;

public record AiRecommendRequest (
        Long memberId, //임시!!!
        String emotionCard,
        String situationCardId
) {}
