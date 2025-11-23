package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "기업 단위 수정 요청 DTO")
public record CompanyUpdateRequest(

        @Schema(description = "기업명", example = "기업 A")
        String name,

        @Schema(description = "기업에 대한 설명", example = "기업 A는 A입니다.")
        String description
) {}
