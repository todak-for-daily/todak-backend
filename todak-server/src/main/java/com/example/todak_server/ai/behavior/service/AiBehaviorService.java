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
import com.example.todak_server.service.SituationCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.example.todak_server.entity.HabitType;
import com.example.todak_server.entity.HabitSenseType;


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
    private final SituationCardService situationCardService;

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

        Map<String, String> habitMap = buildBehaviorHabits(member.getHabits()); // 너가 앞으로 만든 버전

        Map<String, Object> scheduleContext = buildScheduleContext(member);
        String currentTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toString();

        Map<String, Object> payload = aiRequestBuilder.build(dto, habitMap, scheduleContext, currentTime);


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

    private Map<String, String> buildBehaviorHabits(List<Habit> habits) {
        Map<String, List<String>> senseBucket = new java.util.HashMap<>();
        Map<String, String> result = new java.util.LinkedHashMap<>();

        for (Habit habit : habits) {
            if (habit.getType() == null) continue;

            // 공통 설명 텍스트 만들기
            StringBuilder sb = new StringBuilder();

            // 1) 트리거 먼저
            if (habit.getTrigger() != null && !habit.getTrigger().isBlank()) {
                sb.append("나타나는 상황: ").append(habit.getTrigger());
            }

            // 2) 특성 설명
            if (habit.getDescription() != null && !habit.getDescription().isBlank()) {
                if (!sb.isEmpty()) sb.append(" / ");
                sb.append("행동: ").append(habit.getDescription());
            }

            // 3) 안정 행동
            if (habit.getSoothingAction() != null && !habit.getSoothingAction().isBlank()) {
                if (!sb.isEmpty()) sb.append(" / ");
                sb.append("힘들 때: ").append(habit.getSoothingAction());
            }

            String value = sb.isEmpty() ? null : sb.toString();
            if (value == null) continue;

            // 감각(SENSE)
            if (habit.getType() == HabitType.SENSE && habit.getSenseType() != null) {
                String senseKey = "감각-" + toKoreanSenseName(habit.getSenseType()); // 감각-촉각 같은 것
                senseBucket.computeIfAbsent(senseKey, k -> new java.util.ArrayList<>())
                        .add(value);
            }

            // 인지(COGNITION)
            else if (habit.getType() == HabitType.COGNITION) {
                if (habit.getTime() != null && !habit.getTime().isBlank()) {
                    result.put("시간", habit.getTime());
                }
                if (habit.getPlace() != null && !habit.getPlace().isBlank()) {
                    result.put("장소", habit.getPlace());
                }
                if (habit.getTarget() != null && !habit.getTarget().isBlank()) {
                    result.put("대상", habit.getTarget());
                }
                // 인지 설명 + 트리거 + 안정행동이 합쳐진 value
                result.put("인지-설명", value);
            }
        }

        // 감각 버킷 합치기: 각 감각별로 한 줄 string으로 합침
        for (Map.Entry<String, List<String>> entry : senseBucket.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            StringBuilder joined = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) joined.append(" / ");
                joined.append(i + 1).append(") ").append(values.get(i));
            }
            result.put(key, joined.toString());
        }

        return result;
    }



    private String toKoreanSenseName(HabitSenseType senseType) {
        return switch (senseType) {
            case VISUAL      -> "시각";
            case AUDITORY    -> "청각";
            case TASTE       -> "미각";
            case SMELL       -> "후각";
            case TOUCH       -> "촉각";
            case KINESTHETIC -> "운동감각";
        };
    }

    private Map<String, Object> buildScheduleContext(Member member) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<GeneralSchedule> schedules = member.getGeneralSchedules().stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(today))
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();

        GeneralSchedule prev = null;
        GeneralSchedule current = null;
        GeneralSchedule next = null;

        for (int i = 0; i < schedules.size(); i++) {
            GeneralSchedule s = schedules.get(i);
            if (s.getStartTime() != null && s.getEndTime() != null &&
                    (now.isAfter(s.getStartTime()) || now.equals(s.getStartTime())) &&
                    now.isBefore(s.getEndTime())) {
                current = s;
                if (i > 0) prev = schedules.get(i - 1);
                if (i < schedules.size() - 1) next = schedules.get(i + 1);
                break;
            }
        }

        String prevName    = prev    != null ? prev.getTitle() : null;
        String currentName = current != null ? current.getTitle() : null;
        String nextName    = next    != null ? next.getTitle() : null;

        Map<String, Object> ctx = new java.util.HashMap<>();
        ctx.put("prev_name", prevName);
        ctx.put("current_name", currentName);
        ctx.put("next_name", nextName);

        return ctx;
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
    public AiActionDetailResponse getActionDetail(Long memberId, AiActionDetailRequest dto) {
        // 1) context 불러오기
        AiSessionContext context = aiSessionContextService.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("세션 정보 없음"));

// 2) situation 정보
        String situationId = context.getSituationCardId();
        Map<String, Object> situation = situationCardService.getSituationById(situationId);
        String schedule = (String) situation.get("category");
        String condition = (String) situation.get("text");

// 3) emotion
        String emotion = context.getEmotionCard();

// 4) current_time
        String currentTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toString();

// 5) scheduleContext (이전/다음 일정)
        Member member = memberRepository.findById(memberId).orElseThrow();
        Map<String, Object> scheduleContext = buildScheduleContext(member);
        String prev = (String) scheduleContext.get("prev_name");
        String next = (String) scheduleContext.get("next_name");

// 6) characteristic 텍스트
        Map<String, String> habitMap = buildBehaviorHabits(member.getHabits());
        String characteristic = habitMap.toString(); // (지금 임시: 네가 원하면 변환 함수 만들어줌)



        try {
            // 프롬프트 작성
            String prompt = """
                # ROLE
                너는 직장 내 발달장애인(ASD) 대상 '안정 행동' 코치다.
                현재 발달장애인 근로자가 자신의 불안정한 감정을 안정시키기 위해 스스로 선택한 행동을 안전하게 따라 할 수 있도록 그 행동을 1~3단계로 나누어 매우 구체적으로 설명하라.
                JSON 배열(문자열 1~3개)만 출력하라.
                다른 텍스트를 출력하지 마라.
    
            # INPUT
                <INPUT>
                발달장애인 근로자의 특성: %s
                상황: %s 중 %s
                감정: %s
                현재 시간: %s
                현재 일정 이전 일정: %s
                현재 일정 이후 일정: %s
                선택한 행동: %s
                </INPUT>
            
                # CHARACTERISTIC TRANSFORM RULE
                사용자의 특성 입력(type=SENSE 또는 COGNITION 배열)은 아래 규칙에 따라
                기존 characteristic 객체로 변환하여 '발달장애인 근로자의 특성'으로 사용한다.
            
                - type="SENSE"인 경우:
                * 상황 key = trigger
                * value = description 와 soothing_action을 쉼표로 이어 붙인 문자열
                · soothing_action이 비어 있으면 description만 사용
                * 예: {{"trigger":"시끄러울 때","description":"귀를 막음","soothing_action":"조용한 곳으로 가면 괜찮아짐"}} → "시끄러울 때": "귀를 막음, 조용한 곳으로 가면 괜찮아짐"
            
                - type="COGNITION"인 경우:
                * 상황 key = time, place, trigger를 "/"로 이어붙인 문자열
                · trigger가 비어 있으면 "time/place"까지만 사용
                * value = description 와 soothing_action을 쉼표로 이어 붙인 문자열
                · soothing_action이 비어 있으면 description만 사용
                * 예1: time="점심시간 전", place="회사", trigger="배고플 때" → "점심시간 전/회사/배고플 때": "예민해지고 짜증을 많이 냄, 바나나킥 먹기"
                * 예2: time="오후 8시", place="집", trigger="" → "오후 8시/집": "트로트를 들음"
            
                # RULES
                0) 일정/시간 추가 정보 사용 규칙
                - '현재 시간', '현재 일정 이전 일정', '현재 일정 이후 일정'은 선택적인 참고 정보다.
                - 이 값들이 비어 있거나 제공되지 않아도, '발달장애인 근로자의 특성', '상황', '감정', '선택한 행동'만으로
                아래 규칙에 따라 단계 설명을 생성해야 한다.
                - 같은 '발달장애인 근로자의 특성', '상황', '감정', '선택한 행동'이 주어졌을 때
                '현재 시간', '현재 일정 이전 일정', '현재 일정 이후 일정' 값이 달라지더라도
                단계 수(1~3개), 안전 규칙 적용 방식, 문장 형식은 유지해야 한다.
                - 단계 문장 안에 구체적인 시각(예: "오후 8시")이나 일정 이름(예: "퇴근 시간")은
                행동 이해에 꼭 필요한 경우가 아니면 넣지 마라.
            
                1) 단계별 설명 목적
                1-1) 사용자가 선택한 행동을 혼자서 즉시 따라 할 수 있게 1~3단계로 안내하라
                1-2) 각 단계는 행동 절차를 매우 구체적으로 설명하라.
                1-3) 각 단계에는 동사 하나만 들어가도록 써라.
                * 한 단계에 두 가지 일을 섞지 말고, 꼭 나누어 단계 2, 3으로 따로 써라.
                * 예: "수도꼭지를 돌려 미지근한 물이 나오는지 손으로 확인하기"(X)
                → "수도꼭지를 돌리기"(O), "미지근한 물이 나오는지 손으로 확인하기"(O)
                1-4) 행동이 아주 단순하면 1단계만으로 끝내도 된다
                * 예: "주먹 쥐었다 펴기 10번 하기"
            
                2) 기본 안전 규칙 (모든 행동 공통)
                2-1) 위험 가능성이 있는 상황에서는 1단계에 '안전을 먼저 만드는 단계'를 넣어라.
                * 위험 가능성이 있는 상황의 예:
                - 걷는 중이거나 서 있는 채로 하는 행동
                - 버스·지하철·전철 등 이동 수단 안에서 하는 행동
                - 도구(귀마개, 이어폰, 휴대폰, 목도리, 장갑, 우산, 마스크, 음식 등)를 사용하는 행동
                - 주변에 사람이 많거나, 몸이 크게 흔들릴 수 있는 행동
                2-2) 이미 한 자리에 서 있거나, 앉아서 하는 간단한 동작이고 넘어지거나 부딪힐 위험이 거의 없으면 안전 단계 없이 바로 {action}을 설명해도 된다.
                2-3) 안전 단계가 필요한 경우, 1단계에서는 상황에 맞게 다음 중 한 가지 이상을 포함하라.
                * 걷는 중/서 있는 상황:
                - "앞/뒤/오른쪽/왼쪽에 사람이 없는지 보기"
                - "내가 설 자리가 안전한지 보기"
                - "사람/물건이 닿지 않는 자리 찾기"
                - "사람과 닿지 않는 위치에 서기"
                - "내 발 아래에 물/물건이 없는지 보기"
                * 버스/지하철/전철 등 이동 수단 안:
                - "가까운 손잡이 잡기" 또는 두 손을 써야 하는 경우에는 "의자에 앉기"
                - "몸이 흔들리지 않게 손잡이나 의자를 꽉 잡기"
                - "부딪히지 않을 자리가 있는지 보기"
                * 앉아서 하는 일:
                - "의자에 바르게 앉기"
                - "발을 바닥에 붙이기"
                - "앞/뒤/오른쪽/왼쪽에 사람이나 물건이 가까이 있지 않은지 보기"
                2-4) 버스·지하철·전철 등 이동 수단 안에서 휴대폰이나 두 손이 필요한 도구를 사용하는 경우:
                * 가능하면 "의자에 바르게 앉기"를 1단계 또는 초기 단계에 넣어라.
                - 예: "의자에 바르게 앉기"
                * 이동 중에는 화면을 오래 보거나, 걷거나 흔들리면서 두 손 다 쓰는 행동을 만들지 마라.
                - 이 프롬프트에서는 "엄마 사진 잠깐 보기", "음악 듣기"처럼 짧게 사용하는 행동만 설명하라.
                2-5) 도구(귀마개, 이어폰, 목도리, 장갑, 우산, 마스크, 음식 등)를 사용하는 행동일 경우:
                * 휴대폰은 제외 (이 앱을 사용하려면 휴대폰을 하고 있는 것이므로 휴대폰이 있는지 확인할 필요 없음).
                * 선택한 행동 문장에 등장하는 '도구'를 자동으로 찾아라.
                - 예: "귀마개 착용하기" → 도구: 귀마개
                - 예: "목도리로 목 감싸기" → 도구: 목도리
                - 예: "초코빵 먹기" → 도구: 초코빵
                * 도구가 포함된 행동이라면 1단계를 가능한 한 다음 형식으로 써라.
                - "[도구]가 있는지 확인하기 ([도구]가 없다면 ‘뒤로가기’를 누르기)"
                * 그 후 단계에서 실제 도구 사용 동작을 설명하라.
                * 전자기기(휴대폰 등)를 사용하는 행동일 경우, schedule이나 condition에 "세수", "씻기", "샤워", "목욕", "설거지", "청소", "비를 맞", "물이 튀", "젖었" 같은 말이 들어 있을 때만 다른 단계에 "손이 젖었으면 손 닦기"를 한 번 포함하라.
                - 이런 말이 전혀 없다면 굳이 "손이 젖었으면 손 닦기"를 쓰지 마라.
                2-6) 이동이 필요한 경우:
                * "멀리", "빨리" 같은 말은 쓰지 마라.
                * "통로 한가운데"가 아니라 "벽 쪽" 또는 "빈 곳"을 향해 한 걸음만 가도록 안내하라.
                2-7) 절대 금지(부분 일치 포함, 나오면 같은 의미의 안전한 표현으로 바꿔라):
                "주머니에 손","양손 주머니","양손 귀 막기","눈 감고 걷","눈 감고 이동","통로에 서","문 앞","난간","뛰기","달리기","휴대폰 보며 걷","볼륨 크게","콘센트","임의 조작","칼","팔레트","사다리","뜨거운","세제","분무","바닥에 눕","의자 위에 서","창문에 기대","창문에 몸 붙이기"
            
                3) 단계 설명 형식
                3-1) 1단계 → 필요하다면 안전 확보 + 준비, 필요 없으면 바로 핵심 행동 설명
                * 예(안전 단계가 필요한 경우): "앞/뒤에 사람이 없는지 보기"
                * 예(도구 사용): "귀마개가 있는지 확인하기 (귀마개가 없다면 ‘뒤로가기’를 누르기)"
                * 예(이미 안전한 자리에서 하는 간단한 행동일 때): 바로 {action}을 구체적으로 설명해도 된다.
                3-2) 2단계 → 선택한 행동의 핵심 수행
                * {action}의 동작을 그대로 하게 설명하라.
                * {action}에 숫자나 시간(예: "10번","10초","1m")이 있으면, 2단계 또는 마지막 단계 문장에 그 숫자 또는 시간을 반드시 포함하라.
                * 예: 선택한 행동이 "엄마 사진 10초 보기"이면
                - "휴대폰 앨범에서 엄마 사진 찾기"
                - "엄마 사진을 10초 동안 보기"
                * 예: 선택한 행동이 "미지근한 물로 세수하기"이면
                - "수도꼭지를 조금씩 돌리기"
                - "손으로 물 온도 확인하기"
                - "미지근한 물로 얼굴 씻기"
                3-3) 3단계(있는 경우) → 마무리 또는 효과 확인
                * 예: "불빛이 덜 밝은지 보기"
                * 예: "숨을 한 번 깊게 쉬기"
                * 예: "고개 천천히 올려서 정면 보기"
                3-4) 문장 형식
                * 모든 단계 문장은 반드시 '동사 어간 + 하기' 형식으로 끝내라.
                - 예: "주먹을 쥐었다 펴기", "앞에 사람 없는지 보기", "손으로 무릎 누르기"
                * 절대 금지: "~하세요", "~해요", "~하기 위해", "~하면서", "~해 보기", "~할게요"
                * 한 단계 안에 행동 두 개를 붙이는 말도 금지:
                - "그리고", "후에", "나서", "~고", "~면서", "~한 뒤"가 들어가면 안 된다.
                * 단, 2-5에서 지정한 도구 확인 단계에 한해서만 괄호 안 안내 문구를 허용한다:
                - 예: "귀마개가 있는지 확인하기 (귀마개가 없다면 뒤로가기를 누르기)"
                * 그 외 단계는 문장 끝에 마침표나 추가 설명을 붙이지 말고, 순수한 'OOO하기' 한 가지만 쓰라.
            
                4) 말하기 방식(발달장애인 이해도 고려)
                4-1) 초등 저학년이 이해할 수 있는 쉬운 말만 써라.
                4-2) "안전하게", "안정 잡기", "안전한 자리”처럼 추상적인 말을 쓰지 마라.
                * 무엇을 보는지, 무엇을 잡는지, 어디에 앉는지 구체적으로 말하라.
                4-3) "마음 다잡기", "마음을 편안히 하기" 같은 추상적인 표현 대신
                * 몸으로 할 수 있는 행동을 써라.
                - 예: "마음을 편안히 하기"(X) → "숨을 천천히 3번 쉬기"(O)
            
                5) 감정 기반 규칙
                5-1) emotion이 "힘들어서 도움이 필요해요"여도, 이 프롬프트는 추천이 아니라 “이미 선택한 행동의 단계 설명”이므로 "도움 요청하기"를 강제하지 않는다.
                5-2) 단, 선택한 행동이 “도움 요청하기”라면:
                * 주변 사람 한 명을 정해서 보게 하기
                * 어떤 말을 해야 하는지 한 문장으로 알려주기
                * 그 자리에서 기다리도록 안내하라.
                * 예: ["가장 가까운 사람 바라보기","'도와주세요' 한 번 말하기","그 자리에 서서 대답 기다리기"]
            
                6) 최종 점검
                6-1) 걷는 중, 이동 수단, 도구/전자기기 사용, 주변이 붐비는 상황이면
                * 1단계에 "멈추기/앉기/손잡이 잡기/앞뒤 보기" 같은 준비 동작이 있는지 확인하라.
                6-2) 이미 세수 중·책상에 앉음 등으로 충분히 안전한 상태라면
                * 2-2 규칙에 따라 안전 단계를 생략했는지 확인하라.
                6-3) 도구를 사용하는 행동이면
                * 1단계가 "[도구]가 있는지 확인하기 ([도구]가 없다면 ‘뒤로가기’를 누르기)" 형식인지 확인하라.
                * 전자기기를 사용하는 행동이고, schedule이나 condition에 물 관련 단어가 있을 때만 "손이 젖었으면 손 닦기"가 한 번 포함되었는지 확인하라.
                6-4) 선택한 행동에 들어 있는 숫자·시간(예: 10번, 10초, 1m, 3분)을 단계 설명에도 포함했는지 확인하라.
                6-5) 모든 단계에는 금지어(2-7)가 없어야 한다.
                6-6) 단계가 모호하거나 "그리고/또는/만약"이 들어가면 즉시 고쳐라.
                6-7) 한 단계에는 동사 한 개만 쓰라.
                * "돌리기"와 "확인하기"처럼 두 개의 행동이 필요하면, 1단계와 2단계로 나누어 각각 따로 쓰라.
                - 예: "수도꼭지를 돌려 미지근한 물이 나오는지 손으로 확인하기"(X)
                → 1단계 "수도꼭지를 돌리기"(O), 2단계 "미지근한 물이 나오는지 손으로 확인하기"(O)
                6-8) 전체 단계 수는 1~3개가 될 수 있다.
            
                # OUTPUT FORMAT
                ["문자열1"] 또는 ["문자열1","문자열2"] 또는 ["문자열1","문자열2","문자열3"]
                - 응답 전체는 오직 하나의 JSON 배열만 있어야 한다.
                - 배열 밖에는 어떠한 문자도 쓰지 마라. (예: 설명 문장, 라벨, 코드블록 기호, json는 모두 금지)
                - 예시: ["수도꼭지를 돌리기", "손으로 물 온도 확인하기", "미지근한 물로 얼굴 씻기"]
            
                # NOW
                JSON 배열만 출력하라.
                """.formatted(
                    characteristic,   // %s → {characteristic}
                    schedule,         // %s → {schedule}
                    condition,        // %s → {condition}
                    emotion,          // %s → {emotion}
                    currentTime,      // %s → {current_time}
                    prev,             // %s → {prev_schedule}
                    next,             // %s → {next_schedule}
                    dto.selectedAction() // %s → {action}
            );

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
