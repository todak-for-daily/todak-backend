package com.example.todak_server.controller;

import com.example.todak_server.ai.behavior.service.AiSessionContextService;
import com.example.todak_server.dto.request.EmotionSelectRequest;
import com.example.todak_server.dto.response.EmotionCardResponse;
import com.example.todak_server.dto.response.EmotionSelectResponse;
import com.example.todak_server.service.EmotionCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "기능2: 감정 카드 관련 API")
@RestController
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionCardService emotionCardService;
    private final AiSessionContextService aiSessionContextService;

    @Operation(summary = "전체 감정 카드 반환", description = "[{\"id\":\"E001\",\"text\":\"괜찮아요\"},{\"id\":\"E002\",\"text\":\"조금 힘들지만 괜찮아요\"...")
    @GetMapping("/cards")
    public ResponseEntity<List<EmotionCardResponse>> getEmotionCards() {
        return ResponseEntity.ok(emotionCardService.getEmotionCards());
    }

    @Operation(summary = "감정 선택",  description = "사용자가 선택한 감정 카드를 서버로 전달. 감정의 단계(level)에 따라 추천 종료 또는 상황 선택 단계로 분기")
    @PostMapping("/select")
    public ResponseEntity<EmotionSelectResponse> selectEmotion(@AuthenticationPrincipal(expression = "id") Long memberId, @RequestBody EmotionSelectRequest dto) {

        int level = emotionCardService.getLevel(dto.emotionCard());

        if (level >= 4) { // 괜찮아요, 조금 힘들지만 괜찮아요.
            return ResponseEntity.ok(
                    new EmotionSelectResponse("end", "오늘은 괜찮아요 😊 추천이 필요 없어요.")
            );
        }

        // before 감정 저장
        aiSessionContextService.saveOrUpdate(memberId, dto.emotionCard(), null, null);

        return ResponseEntity.ok(
                new EmotionSelectResponse("situation", "조금 힘든 것 같아요. 어떤 상황인가요?")
        );
    }

}
