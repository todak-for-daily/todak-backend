package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CompanyResponse(
        @Schema(description = "기업 ID", example = "1")
        Long id,

        @Schema(description = "기업 이름", example = "기업 A")
        String name,

        @Schema(description = "기업 설명", example = "기업 A는 A입니다.")
        String description
) {}
