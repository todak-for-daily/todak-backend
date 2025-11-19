package com.example.todak_server.dto.request;

public record CautionCreateRequest(
        Long memberId,
        Long managerId,
        String title,
        String description,
        Integer notifyAfterHours
) {}
