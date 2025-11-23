package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직 단위(부서) 수정 요청 DTO")
public record OrganizationUnitUpdateRequest(

        @Schema(description = "기업명", example = "기업 A")
        Long companyId,

        @Schema(description = "조직명", example = "연구개발팀")
        String name,

        @Schema(description = "상위 조직 ID (최상위면 null)", example = "1")
        Long parentId
) {}
