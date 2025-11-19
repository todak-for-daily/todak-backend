package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "접근 권한 업데이트 요청 DTO")
public record AccessPermissionUpdateRequest(

        @Schema(description = "스케줄 보기 가능 여부", example = "true")
        boolean canViewSchedule,

        @Schema(description = "주의사항 보기 가능 여부", example = "true")
        boolean canViewWarning,

        @Schema(description = "건강 데이터 보기 가능 여부", example = "false")
        boolean canViewHealth
) {}
