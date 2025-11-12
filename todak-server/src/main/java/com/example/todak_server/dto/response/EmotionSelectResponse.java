package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmotionSelectResponse(
        @Schema(description = "진행 이어가 상황 카드 받아야한다면 \"situation\", 끝내도 된다면 \"end\"", example = "end")
        String nextStep,   // "situation" or "end"
        @Schema(description = "프론트 표시용 문구", example = "오늘은 괜찮아요. 추천이 필요 없어요.")
        String message     // 프론트 표시용 문구
) {}
