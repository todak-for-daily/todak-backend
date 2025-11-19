package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "직원 등록 요청 DTO")
public record EmployeeRegisterRequest(

        @Schema(description = "직원 이메일", example = "worker@test.com")
        String email,

        @Schema(description = "직원 닉네임", example = "철수")
        String nickname,

        @Schema(description = "소속 조직 Unit ID", example = "3")
        Long organizationUnitId
) {}
