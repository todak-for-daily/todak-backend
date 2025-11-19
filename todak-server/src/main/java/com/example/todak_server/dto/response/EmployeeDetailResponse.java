package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "직원 상세 정보 응답 DTO")
public record EmployeeDetailResponse(

        @Schema(description = "직원 ID", example = "10")
        Long id,

        @Schema(description = "이메일", example = "employee@test.com")
        String email,

        @Schema(description = "닉네임", example = "짱구")
        String nickname,

        @Schema(description = "직책/역할", example = "WORKER")
        String role,

        @Schema(description = "소속 조직명", example = "생산팀A")
        String organizationName,

        @Schema(description = "스케줄 보기 가능 여부", example = "true")
        boolean canViewSchedule,

        @Schema(description = "주의사항 확인 가능 여부", example = "true")
        boolean canViewWarning,

        @Schema(description = "건강 정보 보기 가능 여부", example = "false")
        boolean canViewHealth
) {}
