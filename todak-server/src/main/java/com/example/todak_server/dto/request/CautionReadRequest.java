package com.example.todak_server.dto.request;

public record CautionReadRequest(
        Long memberId,
        Long cautionId
) {}
