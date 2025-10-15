package com.example.todak_server.dto.response;

public record EmotionSelectResponse(
        String nextStep,   // "situation" or "end"
        String message     // 프론트 표시용 문구
) {}
