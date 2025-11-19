package com.example.todak_server.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CautionResponse(
        Long id,
        String title,
        String description,
        String fileUrl,
        boolean isRead,
        Integer notifyAfterHours,
        LocalDateTime createdAt
) {}
