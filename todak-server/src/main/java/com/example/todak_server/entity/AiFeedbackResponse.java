package com.example.todak_server.entity;

public record AiFeedbackResponse(
        FeedbackNextStep nextStep          // 다음 플로우
) {}
