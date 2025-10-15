package com.example.todak_server.dto.request;

public record AiActionDetailRequest(
        Long memberId, // 임시!!!
        String selectedAction
) {
}
