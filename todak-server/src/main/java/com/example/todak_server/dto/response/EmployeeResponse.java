package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "직원 기본 정보 응답 DTO")
public record EmployeeResponse(

        @Schema(description = "직원 ID", example = "10")
        Long id,

        @Schema(description = "이메일", example = "employee@test.com")
        String email,

        @Schema(description = "닉네임", example = "철수")
        String nickname,

        @Schema(description = "직책/역할", example = "WORKER")
        String role,

        @Schema(description = "소속 조직명", example = "생산팀A")
        String organizationName
) {}
