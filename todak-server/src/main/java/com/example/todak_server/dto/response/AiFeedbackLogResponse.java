package com.example.todak_server.dto.response;

import java.time.LocalDateTime;

public record AiFeedbackLogResponse(
        Long id,
        String emotionCard,
        String situationCardId,
        String selectedAction,
        String beforeEmotion,
        String afterEmotion,
        int emotionChangeScore,
        LocalDateTime createdAt
) {}
