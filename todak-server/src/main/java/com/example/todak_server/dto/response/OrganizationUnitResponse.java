package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "조직 구조 트리 응답 DTO")
public record OrganizationUnitResponse(

        @Schema(description = "조직 ID", example = "5")
        Long id,

        @Schema(description = "조직/부서명", example = "생산팀")
        String name,

        @Schema(description = "하위 조직 목록")
        List<OrganizationUnitResponse> children
) {}
