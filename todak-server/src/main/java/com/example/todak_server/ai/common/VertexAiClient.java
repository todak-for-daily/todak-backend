package com.example.todak_server.ai.common;

import com.example.todak_server.ai.behavior.dto.AiRecommendItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class VertexAiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${vertex.project-id}")
    private String projectId;

    @Value("${vertex.location}")
    private String location;

    @Value("${vertex.model}")
    private String model;

    public List<AiRecommendItem> requestRecommendations(Map<String, Object> payload) throws IOException {
        log.info(">>> Vertex request payload = {}", payload);

        String url = String.format(
                "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:generateContent",
                location, projectId, location, model
        );
        log.info(">>> Vertex URL = {}", url);

        // Access Token 발급
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream("vertex-key.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        String accessToken = credentials.getAccessToken().getTokenValue();

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini 형식 바디
        String prompt = makePrompt(payload);

        // RAG 사용 유무 (true면 RAG, false면 vertex-ai)
        boolean useRagForRecommendations = true;
        Map<String, Object> body = buildBodyWithPrompt(prompt, useRagForRecommendations);


        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        log.info(">>> RAG body = {}", body);


        Map<String, Object> res = response.getBody();
        if (res == null || !res.containsKey("candidates")) {
            return List.of(new AiRecommendItem("추천 결과 없음", "⚠️"));
        }
        log.info(">>> Vertex raw response status={}, body={}", response.getStatusCode(), response.getBody());


        // Gemini 응답 파싱
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) res.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String text = (String) parts.get(0).get("text");

        return parseRecommendItems(text);
    }

    @SuppressWarnings("unchecked")
    private String makePrompt(Map<String, Object> payload) {

        // 1. 기본 값 꺼내기
        String emotion = (String) payload.get("emotion_card");

        Map<String, Object> situation = (Map<String, Object>) payload.get("situation_card");
        Map<String, String> behaviorHabits = (Map<String, String>) payload.get("behavior_habits");

        // 새 구조: schedule_context + current_time
        Map<String, Object> scheduleContext = (Map<String, Object>) payload.get("schedule_context");
        String currentTime = (String) payload.get("current_time");

        // 2. 상황카드 정보
        String situationCategory = null;
        String condition = null;
        if (situation != null) {
            situationCategory = (String) situation.get("category"); // ENV / BODY / ACT / COMM / MIND / MISC
            condition = (String) situation.get("text");
        }

        // 3. 행동 특성 텍스트 합치기
        StringBuilder habitsText = new StringBuilder();
        if (behaviorHabits != null) {
            behaviorHabits.forEach((key, behavior) -> {
                habitsText.append(String.format("%s: %s%n", key, behavior));
            });
        }
        String characteristic = habitsText.toString().trim();
        if (characteristic.isEmpty()) {
            characteristic = "등록된 행동 특성이 아직 없습니다.";
        }

        // 4. 일정 컨텍스트 (이전/현재/다음)
        String prevName = null;
        String currentName = null;
        String nextName = null;

        if (scheduleContext != null) {
            prevName = (String) scheduleContext.get("prev_name");
            currentName = (String) scheduleContext.get("current_name");
            nextName = (String) scheduleContext.get("next_name");
        }

        String safePrev   = (prevName   == null || prevName.isBlank())   ? "없음" : prevName;
        String safeCurr   = (currentName == null || currentName.isBlank()) ? "현재 일정 없음" : currentName;
        String safeNext   = (nextName   == null || nextName.isBlank())   ? "없음" : nextName;
        String safeTime   = (currentTime == null || currentTime.isBlank()) ? "현재 시간 정보 없음" : currentTime;
        String safeCategory = (situationCategory == null || situationCategory.isBlank()) ? "미지정" : situationCategory;
        String safeCondition = (condition == null || condition.isBlank()) ? "설명 없음" : condition;
        String safeEmotion   = (emotion == null || emotion.isBlank()) ? "알 수 없음" : emotion;

        return """
      # ROLE
       너는 직장 내 발달장애인(ASD) 대상 '안정 행동' 코치다.
       현재 발달장애인 근로자가 처한 상황에서 이 근로자를 안정시키기 위한 행동을 추천하라.
       JSON 객체 하나만 출력하라.
       형식: json {{"actions":[{{"title":"문자열","emojis":"문자열"}}, ...]}}
       다른 텍스트를 출력하지 마라.
    
       # INPUT
       <INPUT>
       발달장애인 근로자의 특성: {characteristic}
       상황: {schedule} 중 {condition}
       감정: {emotion}
       현재 시간: {current_time}
       현재 일정 이전 일정: {prev_schedule}
       현재 일정 이후 일정: {next_schedule}
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
       0) 감정 기반 도움요청 규칙
       - 감정이 정확히 "힘들어서 도움이 필요해요"일 경우:
       * 추천 1~2: E/B/C에서 선택
       * 추천 3: 반드시 문자열 그대로 "도움 요청하기"
       * "도움 요청하기"는 숫자/도구/장소/금지어/치환 검사에서 제외
       - 감정이 정확히 "도움이 필요해요"가 아니면:
       * "도움 요청하기" 및 다음 패턴이 부분 일치해도 절대 추천 금지:
       ["도움 요청", "도움을 요청", "도와달", "도움이 필요", "도움 부탁", "도와 주세요", "도와주세요"]
    
       0-1) 일정/시간 추가 정보 사용 규칙
       - '현재 시간', '현재 일정 이전 일정', '현재 일정 다음 일정'은 선택적인 참고 정보다.
       - 이 값들이 비어 있거나 제공되지 않아도, '발달장애인 근로자의 특성', '상황', '감정'만으로
       기존 프롬프트와 동일한 방식으로 행동을 추천해야 한다.
       - 같은 '발달장애인 근로자의 특성', '상황', '감정'이 주어졌을 때
       '현재 시간', '현재 일정 이전 일정', '현재 일정 다음 일정' 값이 달라지더라도
       추천 행동의 기본 구조(3개 수, 카테고리 구성, 안전 규칙 적용 방식)는 유지해야 한다.
       - title에는 일정 이름이나 시간('현재 시간', '현재 일정 이전 일정', '현재 일정 다음 일정')을 직접 넣지 마라.
       (예: "점심시간 전 간식 먹기"처럼 구체 일정명을 넣지 말고, "바나나킥 5개 먹기"처럼 행동만 표현한다.)
       - type="COGNITION" 특성 중 time 값이 '현재 시간' 기준 ±10분 이내이면 참고 가능한 보조 정보로만 사용한다.
    
       1) 독립 선택지 & 카테고리 다양성(순서 없음):
       - 세 행동은 서로 독립적인 선택지다(연속 수행 아님).
       - 세 행동 중 최소 2개 이상의 서로 다른 카테고리(E=환경, B=신체, C=인지)를 포함하라.
       - 이동/작업 상황이면 E 또는 B 중 하나를 반드시 포함하라.
    
       2) 개인 전략 우선
       - characteristic에 기록된 특성 중 '현재 맥락에 도움이 되는 안전한' 행동을 우선 포함하라.
       - 개인 전략이 이동/작업 맥락에서 시야·청각·균형을 크게 차단하면, 같은 카테고리(E/B/C)의 안전 대체(~하기)로 즉시 치환하라.
    
       3) 각 행동은 도구/장소/숫자를 포함하도록 노력하라.
       - 도구는 소지·허용 시에만 제안하라(미소지/미허용 시 동일 카테고리 대체 동작 사용).
       - 이동/작업 중 안전을 위해 '계속 유지'가 필요한 동작(E/B)은 숫자를 생략해도 된다
       (예: "손잡이 꽉 잡기", "벽쪽 자리 서 있기", "안전모 계속 쓰기").
       - 단, 정지 상태 과제나 인지 전략(C)은 숫자를 써서 끝이 보이게 하라(예: "눈앞 점 10초 보기").
    
       4) 위험 금지 및 자동 치환(부분 일치 금지어 적용):
       - 다음 문자열이 포함되는 행동은 절대 추천하지 마라:
       "주머니에 손","양손 주머니","양손 귀 막기","눈 감고 걷","눈 감고 서","눈 감고 이동",
       "통로에 서","문 앞","난간","손 놓고","빠르게 이동","뛰기","달리기",
       "휴대폰 보며 걷","볼륨 크게","콘센트","임의 조작","칼","끓는","뜨거운",
       "지게차","사다리","세제","모르는 사람 촬영","타인 신체 만지",
       "바닥에 눕","의자 위에 서","창문/창가에 기대","창문/창가에 몸 붙이기"
       - 허용 리스트 밖 표현이 생성되면 가장 가까운 허용 표현으로 치환하라:
       (E)["빈 곳 쪽으로 가기","벽 쪽으로 한 걸음 가기","불빛 1칸 낮추기","소리 2칸 줄이기"]
       (B)["손잡이 꽉 잡기","무릎 위 손 얹고 10초 누르기","주먹 쥐었다 펴기 10번 하기", "심호흡 5번 하기"]
       (C)["다음 정거장 확인하기","눈앞 점 10초 보기"]
       - 이동/작업 중에는 시야·청각·균형을 크게 차단하는 동작 금지(예: 양손 주머니, 양손 귀 막기, 눈 오래 감기, 큰 몸 흔들기).
       - 이동 동작은 '거리/걸음'으로 표현하고 '분 단위 걷기' 지시 금지(예: 1분 걷기 금지).
       - '문/출입구/통로/창고 문 옆' 정지 금지: 해당 문구 포함 시 동일 카테고리 안전 대체로 치환.
       - 귀마개/선글라스/눈 감기/응시 고정은 '정지 상태'에서만 허용(이동·작업 중 금지).
    
       5) 문장 형식(= title 규칙):
       - title은 동사구 명사형(어간+"기")으로 끝낼 것(예: "심호흡하기"), 6~20자, 마침표·이모지 금지.
       - 숫자는 구체적으로(초/분/번/개/걸음).
       - 쉬운 낱말만 사용하라(초등 저학년 수준 어휘).
       - 한 가지 행동만 말하라(그리고/또는/만약 등 연결어 금지).
       - 부정형(~하지 않기) 대신 긍정형(~하기)로 쓰기.
       - 모호어 금지: "잠시/적당히/가끔/천천히만" 등은 숫자로 바꿔라(예: "10초", "3번").
       - 예외: 3)의 ‘연속 유지형’ E/B 동작은 숫자 없이 허용한다.
    
       6) emojis 필드 형식:
       - emojis는 발달장애인이 title이 어떤 행동을 나타내는지 이해하기 쉽게 간단한 이모지로 나타내라.
       예: title이 "벽쪽으로 1걸음 이동하기"이면 emojis는 "🚶➡️🧱" 형식.
       - 이모지는 행동의 느낌을 살리는 간단한 이모지를 사용하되, 추가 설명 텍스트는 넣지 마라.
    
       7) 최종 안전 셀프-체크(출력 전 내부 점검):
       - 세 항목 모두에 금지어가 없는지 검사하라.
       - 이동/작업 맥락이면 최소 하나가 E 또는 B인지 확인하라.
       - 감정이 "도움이 필요해요"인지 검사:
       * "도움이 필요해요"인 경우, 3번째 title이 정확히 "도움 요청하기"인지 확인하라.
       * "도움이 필요해요"가 아닌 경우, 세 항목 title에 ["도움 요청","도움을 요청","도와달","도움이 필요","도움 부탁","도와 주세요","도와주세요"]가 포함되지 않았는지 확인하라.
       - 위반 항목은 동일 카테고리(E/B/C)의 안전 대체(~하기)로 즉시 교체하라.
       - 교체 후 다시 검사하라. 여전히 위반이면 기본 세트로 폴백:
       [”무릎 위 손 얹고 10초 누르기", "주먹 쥐었다 펴기 10번 하기", "심호흡 5번 하기", "눈앞 점 10초 보기"]
       이때 title에는 위 문자열을 쓰고, emojis에는 각 title을 잘 표현하는 간단한 이모지로 나타내라.
    
       # OUTPUT FORMAT
       json {{"actions":[{{"title":"문자열","emojis":"문자열"}},{{"title":"문자열","emojis":"문자열"}},{{"title":"문자열","emojis":"문자열"}}]}}
       - JSON 객체 외의 설명/접두어/줄바꿈/코드펜스/주석을 출력하지 마라(순수 JSON만).
       - \\n는 넣지 마라.
    
       # NOW
       위 형식의 JSON만 출력하라.
    """.formatted(
                characteristic,   // %s → characteristic
                safeCategory,     // %s → schedule
                safeCondition,    // %s → condition
                safeEmotion,      // %s → emotion
                safeTime,         // %s → current_time
                safePrev,         // %s → prev_schedule
                safeNext          // %s → next_schedule
        );
    }

    private List<String> parseActions(String text) {
        try {
            // 1️코드블록(```json ... ```) 제거
            String cleaned = text
                    .replaceAll("(?s)```json", "")  // '```json' 태그 제거
                    .replaceAll("(?s)```", "")      // 마지막 '```' 제거
                    .trim();

            // 2️혹시 JSON 배열로 안 시작하면 감싸기
            if (!cleaned.startsWith("[")) {
                cleaned = "[" + cleaned + "]";
            }

            // 3️JSON 문자열을 실제 List<String>으로 파싱
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(cleaned, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 파싱 실패 시 로그 찍고 원본을 반환
            System.err.println("Gemini 응답 파싱 실패: " + e.getMessage());
            return List.of(text);
        }
    }

    public List<String> requestActionSteps(Map<String, Object> payload) throws IOException {
        // 같은 generateContent 엔드포인트 재사용
        String url = String.format(
                "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:generateContent",
                location, projectId, location, model
        );

        // Access Token 발급
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream("vertex-key.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        String accessToken = credentials.getAccessToken().getTokenValue();

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        boolean useRagForSteps = true;
        Map<String, Object> body = buildBodyFromPayload(payload, useRagForSteps);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> res = response.getBody();
        if (res == null || !res.containsKey("candidates")) {
            return List.of("단계별 가이드 없음");
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) res.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String text = (String) parts.get(0).get("text");

        return parseActions(text); // 기존 파싱 재사용
    }

    @SuppressWarnings("unchecked")
    public List<AiRecommendItem> parseRecommendItems(String text) {
        try {
            String cleaned = text
                    .replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .trim();

            ObjectMapper mapper = new ObjectMapper();

            // 1) 루트는 { "actions": [ ... ] } 형태의 객체
            Map<String, Object> root = mapper.readValue(
                    cleaned,
                    new TypeReference<Map<String, Object>>() {}
            );

            Object actionsNode = root.get("actions");
            if (!(actionsNode instanceof List<?> rawList)) {
                log.error("actions 필드가 배열이 아님: {}", cleaned);
                return List.of(new AiRecommendItem("추천 결과 없음", "⚠️"));
            }

            // 2) actions 배열 안의 각 요소는 { "title": "...", "emojis": "..." }
            List<AiRecommendItem> result = rawList.stream()
                    .filter(Map.class::isInstance)
                    .map(o -> (Map<String, Object>) o)
                    .map(m -> {
                        String title = (String) m.getOrDefault("title", m.get("action"));
                        String emojis = (String) m.getOrDefault("emojis", "");
                        return new AiRecommendItem(title, emojis);
                    })
                    .toList();

            if (result.isEmpty()) {
                return List.of(new AiRecommendItem("추천 결과 없음", "⚠️"));
            }

            return result;
        } catch (Exception e) {
            log.error("Gemini 추천 파싱 실패: {}", e.getMessage(), e);
            return List.of(new AiRecommendItem("추천 결과 없음", "⚠️"));
        }
    }


    // --------------------------------- RAG 관련 ------------------------------------

    private static final String RAG_CORPUS = "projects/todak-for-daily/locations/asia-northeast3/ragCorpora/6917529027641081856";

    private Map<String,Object> buildRagTool() {
        Map<String, Object> ragResource = Map.of("ragCorpus", RAG_CORPUS);

        Map<String,Object> ragRetrievalConfig = Map.of("topK",10);

        Map<String, Object> vertexRagStore = Map.of(
                "ragResources", List.of(ragResource),
                "ragRetrievalConfig", ragRetrievalConfig
        );

        Map<String, Object> retrieval = Map.of(
                "vertexRagStore", vertexRagStore
        );

        return Map.of(
                "retrieval", retrieval
        );
        }

    // RAG 사용 유무에 따라 (추천 행동 3개)
    private Map<String, Object> buildBodyWithPrompt(String prompt, boolean useRag) {
        Map<String, Object> contents = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", prompt))
        );

        if (useRag) {
            return Map.of(
                    "contents", List.of(contents),
                    "tools", List.of(buildRagTool())
            );
        } else {
            return Map.of(
                    "contents", List.of(contents)
            );
        }
    }


    //RAG 사용 유무에 따라 (단계별 가이드: 이미 payload가 body인 경우)
    @SuppressWarnings("unchecked")
    private Map<String, Object> buildBodyFromPayload(Map<String, Object> payload, boolean useRag) {
        if(!useRag) {
            return payload;
        }
        Map<String, Object> body = new java.util.HashMap<>(payload);
        body.put("tools", List.of(buildRagTool()));
        return body;
    }



}