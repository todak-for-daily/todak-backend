package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "변경 사항 로그 응답 DTO")
public record ChangeLogResponse(

        @Schema(description = "변경 로그 ID", example = "21")
        Long changeLogId,

        @Schema(description = "카테고리", example = "근무 스케줄")
        String category,

        @Schema(description = "변경된 필드명", example = "startTime")
        String fieldName,

        @Schema(description = "기존 값", example = "09:00")
        String oldValue,

        @Schema(description = "변경 후 값", example = "10:00")
        String newValue,

        @Schema(description = "변경된 시각", example = "2025-11-18T11:22:33")
        LocalDateTime changedAt,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead
) {}
