package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주의사항 읽음 처리 요청 DTO")
public record CautionReadRequest(

        @Schema(description = "직원 ID", example = "3")
        Long memberId,

        @Schema(description = "주의사항 ID", example = "8")
        Long cautionId
) {}
