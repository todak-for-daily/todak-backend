package com.example.todak_server.ai.behavior.controller;

import com.example.todak_server.ai.behavior.dto.request.AiFeedbackRequest;
import com.example.todak_server.ai.behavior.dto.request.AiRecommendRequest;
import com.example.todak_server.ai.behavior.dto.response.AiActionDetailResponse;
import com.example.todak_server.ai.behavior.dto.response.AiRecommendResponse;
import com.example.todak_server.ai.behavior.service.AiBehaviorService;
import com.example.todak_server.ai.behavior.service.AiFeedbackService;
import com.example.todak_server.ai.behavior.service.AiSessionContextService;
import com.example.todak_server.dto.request.AiActionDetailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiBehaviorController {

    private final AiBehaviorService aiBehaviorService;
    private final AiSessionContextService aiSessionContextService;
    private final AiFeedbackService aiFeedbackService;

    // 감정/상황 기반 추천 행동 3가지 받아오기
    @PostMapping("/recommend")
    public ResponseEntity<AiRecommendResponse> recommend(@RequestBody AiRecommendRequest dto) {
        Long memberId = 2L; // 임시로 고정 (JWT 붙기 전까지만)

        AiRecommendResponse response = aiBehaviorService.getRecommendations(dto);
        aiSessionContextService.saveOrUpdate(memberId, null, dto.situationCardId(), null);
        return ResponseEntity.ok(response);
    }

    // 선택 행동에 대한 세부 단계 받아오기
    @PostMapping("/action-detail")
    public ResponseEntity<AiActionDetailResponse> actionDetail(@RequestBody AiActionDetailRequest dto) {
        Long memberId = 2L; // 임시로 고정 (JWT 붙기 전까지만)

        aiSessionContextService.saveOrUpdate(memberId, null, null, dto.selectedAction());
        return ResponseEntity.ok(aiBehaviorService.getActionDetail(dto));
    }

//    @PostMapping("/recommend")
//    public ResponseEntity<AiRecommendResponse> recommend(
//            @AuthenticationPrincipal CustomUserDetails user,
//            @RequestBody AiRecommendRequest dto
//    ) {
//        Long memberId = user.getMemberId();
//        AiRecommendResponse response = aiBehaviorService.getRecommendations(dto);
//        aiSessionContextService.saveOrUpdate(memberId, dto.emotionCard(), dto.situationCardId(), null);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/action-detail")
//    public ResponseEntity<AiActionDetailResponse> actionDetail(
//            @AuthenticationPrincipal CustomUserDetails user,
//            @RequestBody AiActionDetailRequest dto
//    ) {
//        Long memberId = user.getMemberId();
//        aiSessionContextService.saveOrUpdate(memberId, null, null, dto.selectedAction());
//        return ResponseEntity.ok(aiBehaviorService.getActionDetail(dto));
//    }

    // after 피드백 받은 후 점수 결과+보낸 데이터를 db에 저장.
    @PostMapping("/feedback")
    public ResponseEntity<Void> saveFeedback(@RequestBody AiFeedbackRequest dto) {
        Long memberId = 2L; // 임시로 고정 (JWT 붙기 전까지만)
        aiFeedbackService.saveFeedback(memberId, dto.afterEmotion());
        return ResponseEntity.ok().build(); // 단순 성공 응답
    }

}
