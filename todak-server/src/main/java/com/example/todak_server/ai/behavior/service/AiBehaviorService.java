package com.example.todak_server.ai.behavior.service;

import com.example.todak_server.ai.behavior.dto.request.AiRecommendRequest;
import com.example.todak_server.ai.behavior.dto.response.AiActionDetailResponse;
import com.example.todak_server.ai.behavior.dto.response.AiRecommendResponse;
import com.example.todak_server.ai.common.VertexAiClient;
import com.example.todak_server.dto.request.AiActionDetailRequest;
import com.example.todak_server.entity.AiSessionContext;
import com.example.todak_server.entity.GeneralSchedule;
import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.service.EmotionCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import com.example.todak_server.entity.Habit;


@Service
@RequiredArgsConstructor
public class AiBehaviorService {

    private final MemberRepository memberRepository;
    private final AiRequestBuilder aiRequestBuilder;
    private final VertexAiClient vertexAiClient;
    private final EmotionCardService emotionCardService;
    private final AiSessionContextService aiSessionContextService;

    public AiRecommendResponse getRecommendations(AiRecommendRequest dto) {
        Long memberId = dto.memberId();

        // 기존 감정 정보 복원
        AiSessionContext context = aiSessionContextService.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("이전 감정 정보가 없습니다."));

        String emotionCard = context.getEmotionCard();
        String situationCardId = dto.situationCardId();
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        List<String> habits = member.getHabits()
                .stream()
                .map(Habit::getContent)
                .toList();

        String currentSchedule = findCurrentSchedule(member);

        Map<String, Object> payload = aiRequestBuilder.build(dto, habits, currentSchedule);

        List<String> actions;
        try {
            actions = vertexAiClient.requestRecommendations(payload);
        } catch (IOException e) {
            // 예외 로그 출력하고 기본 응답 리턴
            e.printStackTrace();
            actions = List.of("AI 요청 중 오류 발생");
        }

        return new AiRecommendResponse(actions);
    }


    // 현재 시간에 포함되는 일정 반환 / 없다면 "현재 일정 없음"
    private String findCurrentSchedule(Member member) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return member.getGeneralSchedules().stream()
                .filter(schedule ->
                        schedule.getDate() != null &&
                                schedule.getDate().equals(today) &&
                                schedule.getStartTime() != null &&
                                schedule.getEndTime() != null &&
                                (now.isAfter(schedule.getStartTime()) || now.equals(schedule.getStartTime())) &&
                                now.isBefore(schedule.getEndTime()))
                .findFirst()
                .map(GeneralSchedule::getTitle)
                .orElse("현재 일정 없음");
    }

    // 선택 행동에 대한 세부 단계 요청
    public AiActionDetailResponse getActionDetail(AiActionDetailRequest dto) {
        try {
            // 프롬프트 작성
            String prompt = String.format("""
                사용자가 선택한 행동은 '%s'입니다.
                이 행동을 단계별로 수행하기 위한 구체적인 가이드를 3~5단계로 작성해주세요.
                예시 형식:
                ["조용한 곳을 찾는다", "자세를 바르게 한다", "눈을 감고 깊게 숨을 들이마신다", "3회 반복한다"]
                JSON 배열로만 반환해주세요.
                """, dto.selectedAction());

            // Vertex AI 요청 바디 구성
            Map<String, Object> payload = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(Map.of("text", prompt))
                            )
                    )
            );

            // Vertex AI 호출
            List<String> steps = vertexAiClient.requestActionSteps(payload);

            return new AiActionDetailResponse(dto.selectedAction(), steps);
        } catch (Exception e) {
            e.printStackTrace();
            return new AiActionDetailResponse(dto.selectedAction(), List.of("단계별 가이드 생성 실패"));
        }
    }

}
