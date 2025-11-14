package com.example.todak_server.ai.behavior.controller;

import com.example.todak_server.ai.behavior.dto.request.AiFeedbackRequest;
import com.example.todak_server.ai.behavior.dto.request.AiRecommendRequest;
import com.example.todak_server.ai.behavior.dto.response.AiActionDetailResponse;
import com.example.todak_server.ai.behavior.dto.response.AiRecommendResponse;
import com.example.todak_server.ai.behavior.service.AiBehaviorService;
import com.example.todak_server.ai.behavior.service.AiFeedbackService;
import com.example.todak_server.ai.behavior.service.AiSessionContextService;
import com.example.todak_server.dto.request.AiActionDetailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "기능2: 감정카드 선택 후 행동들 관련 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiBehaviorController {

    private final AiBehaviorService aiBehaviorService;
    private final AiSessionContextService aiSessionContextService;
    private final AiFeedbackService aiFeedbackService;

    // 감정/상황 기반 추천 행동 3가지 받아오기
    @Operation(
            summary = "감정/상황 기반 행동 추천 3가지",
            description = "사용자가 선택한 감정/상황 카드를 기반으로 3가지 추천 행동을 반환."
    )
    @PostMapping("/recommend")
    public ResponseEntity<AiRecommendResponse> recommend( @AuthenticationPrincipal(expression = "id") Long memberId, @RequestBody AiRecommendRequest dto) {

        log.info(">>> /api/recommend controller CALLED, memberId={}", memberId);

        AiRecommendResponse response = aiBehaviorService.getRecommendations(memberId,dto);
        aiSessionContextService.saveOrUpdate(memberId, null, dto.situationCardId(), null);
        return ResponseEntity.ok(response);
    }

    // 선택 행동에 대한 세부 단계 받아오기
    @Operation(summary = "선택한 행동 세부 단계 조회", description = "사용자가 선택한 추천행동에 대한 세부 단계를 반환")
    @PostMapping("/action-detail")
    public ResponseEntity<AiActionDetailResponse> actionDetail(@AuthenticationPrincipal(expression = "id") Long memberId, @RequestBody AiActionDetailRequest dto) {
        aiSessionContextService.saveOrUpdate(memberId, null, null, dto.selectedAction());
        return ResponseEntity.ok(aiBehaviorService.getActionDetail(dto));
    }


    // after 피드백 받은 후 점수 결과+보낸 데이터를 db에 저장.
    @Operation(summary = "피드백 저장", description = "행동 후 사용자의 감정(afterEmotion) 받아 점수화 후 결과들을 DB에 저장")
    @PostMapping("/feedback")
    public ResponseEntity<Void> saveFeedback(@AuthenticationPrincipal(expression = "id") Long memberId,@RequestBody AiFeedbackRequest dto) {
        aiFeedbackService.saveFeedback(memberId, dto.afterEmotion());
        return ResponseEntity.ok().build(); // 단순 성공 응답
    }

}
