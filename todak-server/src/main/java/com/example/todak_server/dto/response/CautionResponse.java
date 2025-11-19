package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "주의사항 조회 응답 DTO")
public record CautionResponse(

        @Schema(description = "주의사항 ID", example = "7")
        Long id,

        @Schema(description = "제목", example = "고온 작업 시 보호구 착용 필수")
        String title,

        @Schema(description = "내용 설명", example = "용접 구역에서는 반드시 보안경과 방열 장갑을 착용해야 합니다.")
        String description,

        @Schema(description = "파일 URL(이미지 또는 동영상)", example = "https://storage.googleapis.com/.../warning01.jpg")
        String fileUrl,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead,

        @Schema(description = "읽지 않을 경우 몇 시간 뒤 알림 보낼지", example = "6")
        Integer notifyAfterHours,

        @Schema(description = "등록 시각", example = "2025-11-18T12:33:11")
        LocalDateTime createdAt
) {}
