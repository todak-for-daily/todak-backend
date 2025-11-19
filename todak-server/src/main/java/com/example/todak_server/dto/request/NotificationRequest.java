package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "공지/알림 전송 요청 DTO")
public record NotificationRequest(

        @Schema(description = "공지 발송 관리자 ID", example = "1")
        Long adminId,

        @Schema(description = "공지 제목", example = "근무 환경 점검 안내")
        String title,

        @Schema(description = "공지 내용", example = "오늘 중으로 근무환경 체크해주세요.")
        String body,

        @Schema(description = "대상 멤버 ID 목록. 비어있으면 전체 발송", example = "[3, 5, 10]")
        List<Long> targetMemberIds
) {}
