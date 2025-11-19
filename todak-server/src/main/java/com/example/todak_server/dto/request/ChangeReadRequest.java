package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "변경사항 읽음 처리 요청 DTO")
public record ChangeReadRequest(

        @Schema(description = "읽음 처리할 변경 로그 ID 목록", example = "[2, 3, 7]")
        List<Long> changeLogIds,

        @Schema(description = "직원 memberId", example = "5")
        Long memberId
) {}
