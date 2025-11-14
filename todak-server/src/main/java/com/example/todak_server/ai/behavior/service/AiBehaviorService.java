package com.example.todak_server.ai.behavior.service;

import com.example.todak_server.ai.behavior.dto.AiRecommendItem;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.todak_server.entity.Habit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiBehaviorService {

    private final MemberRepository memberRepository;
    private final AiRequestBuilder aiRequestBuilder;
    private final VertexAiClient vertexAiClient;
    private final EmotionCardService emotionCardService;
    private final AiSessionContextService aiSessionContextService;

    // 위험 행동 리스트
    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "주머니에 손","양손 주머니","양손 귀 막기",
            "눈 감고 걷","눈 감고 서","눈 감고 이동",
            "통로에 서","문 앞","난간",
            "손 놓고","빠르게 이동","뛰기","달리기",
            "휴대폰 보며 걷","콘센트","임의 조작","칼",
            "끓는","뜨거운","지게차","랙","팔레트","사다리",
            "세제","분무","모르는 사람 촬영","타인 신체 만지",
            "바닥에 눕","의자 위에 서"
    );

    // 폴백할 기본 리스트
    private static final List<AiRecommendItem> SAFE_FALLBACK_SET = List.of(
            new AiRecommendItem("벽 쪽으로 한 걸음 가기", "🚶➡️🧱"),
            new AiRecommendItem("손잡이 꽉 잡기 10초 하기", "🤲🪑"),
            new AiRecommendItem("타이머 3분 켜기", "⏱️")
    );
    private final ResourceLoader resourceLoader;

    public AiRecommendResponse getRecommendations(Long memberId, AiRecommendRequest dto) {
        log.info(">>> getRecommendations CALLED, memberId={}", memberId);


        // 기존 감정 정보 복원
        AiSessionContext context = aiSessionContextService.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("이전 감정 정보가 없습니다."));

        String emotionCard = context.getEmotionCard();
        String situationCardId = dto.situationCardId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Map<String, String> habitMap = member.getHabits().stream()
                .collect(Collectors.toMap(
                        Habit::getSituation,
                        Habit::getContent,
                        (oldValue, newValue) -> newValue // 같은 key면 마지막 값으로 덮어쓰기
                ));

        String currentSchedule = findCurrentSchedule(member);

        Map<String, Object> payload = aiRequestBuilder.build(dto, habitMap, currentSchedule);

        List<AiRecommendItem> actions;
        try {
            actions = vertexAiClient.requestRecommendations(payload);
        } catch (IOException e) {
            // 예외 로그 출력하고 기본 응답 리턴
            e.printStackTrace();
            actions = List.of(new AiRecommendItem("AI 요청 중 오류 발생", "⚠️"));
        }

        List<AiRecommendItem> safeActions = sanitizeAndFallback(actions); // 위험 행동 차단 및 기본 행동 폴백

        return new AiRecommendResponse(safeActions);
    }

    List<AiRecommendItem> sanitizeAndFallback(List<AiRecommendItem> raw) {

        // 1. 금지어 포함된 것 제거
        List<AiRecommendItem> filtered = raw.stream()
                .filter(item -> item.action() != null && !item.action().isBlank())
                .filter(item -> !isForbidden(item))
                .toList();

        // 2. 중복 제거 (동일한 Action 텍스트는 하나만 되도록.)
        List<AiRecommendItem> deduped = filtered.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                AiRecommendItem::action,
                                item -> item,
                                (a,b) -> a // 중복 시 첫번째 거 유지
                        ),
                        m -> m.values().stream().toList()
                ));
        List<AiRecommendItem> result = new java.util.ArrayList<>(deduped);

        // 3. 최소 3개 채우기 (부족하면 기본 세트에서 보충)
        if(result.size() < 3) {
            var existingTitles = result.stream()
                    .map(AiRecommendItem::action)
                    .collect(Collectors.toList());

            for(AiRecommendItem safe : SAFE_FALLBACK_SET) {
                if(result.size() >= 3) break;
                if(!existingTitles.contains(safe.action())) {
                    result.add(safe);
                }
            }
        }

        // 4. 그래도 아무것도 없으면 기본 세트 전체 사용
        if(result.isEmpty()) {
            result = new java.util.ArrayList<>(SAFE_FALLBACK_SET);
        }

        // 3개까지만 잘라서 return
        return result.subList(0, Math.min(3, result.size()));
    }

    private boolean isForbidden(AiRecommendItem item) {
        String title = item.action();
        return FORBIDDEN_KEYWORDS.stream().anyMatch(title::contains);
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

            return new AiActionDetailResponse(
                    dto.selectedAction(),
                    dto.selectedEmojis(), // 프론트에서 들어온 값 그대로 쓰기
                    steps
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new AiActionDetailResponse(
                    dto.selectedAction(),
                    dto.selectedEmojis(),
                    List.of("단계별 가이드 생성 실패")
            );
        }
    }

}
