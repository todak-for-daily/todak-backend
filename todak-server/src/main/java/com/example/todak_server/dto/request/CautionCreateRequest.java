package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주의사항 생성 요청 DTO")
public record CautionCreateRequest(

        @Schema(description = "주의사항 대상 직원 ID", example = "3")
        Long memberId,

        @Schema(description = "등록한 관리자 ID", example = "1")
        Long managerId,

        @Schema(description = "주의사항 제목", example = "고온 장비 작업 주의")
        String title,

        @Schema(description = "주의사항 상세 설명", example = "용접 작업 시 보호 장비를 반드시 착용해야 합니다.")
        String description,

        @Schema(description = "지정 시간 내 미확인 시 알림 보낼 시간(시간 단위)", example = "6")
        Integer notifyAfterHours
) {}
