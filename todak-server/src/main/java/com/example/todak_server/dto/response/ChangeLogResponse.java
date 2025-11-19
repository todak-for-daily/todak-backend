package com.example.todak_server.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChangeLogResponse(
        Long changeLogId,
        String category,
        String fieldName,
        String oldValue,
        String newValue,
        LocalDateTime changedAt,
        boolean isRead   // 읽음 여부
) {}
