package com.example.todak_server.ai.common;

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

    public List<String> requestRecommendations(Map<String, Object> payload) throws IOException {
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
            return List.of("추천 결과 없음");
        }
        log.info(">>> Vertex raw response status={}, body={}", response.getStatusCode(), response.getBody());


        // Gemini 응답 파싱
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) res.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String text = (String) parts.get(0).get("text");

        return parseActions(text);
    }

    private String makePrompt(Map<String, Object> payload) {

        String emotion = (String) payload.get("emotion_card");
        Map<String, Object> situation = (Map<String, Object>) payload.get("situation_card");
        Map<String, String> behaviorHabits = (Map<String, String>) payload.get("behavior_habits");
        String schedule = (String) payload.get("current_schedule");

        // 발달장애인 근로자의 특성: 상황별 행동 패턴을 문자열로 정리
        StringBuilder habitsText = new StringBuilder();
        behaviorHabits.forEach((situationKey, behavior) -> {
            habitsText.append(String.format("%s: %s\n", situationKey, behavior));
        });
        String characteristic = habitsText.toString().trim();

        String condition = (String) situation.get("text");

        return """
        # ROLE
        너는 직장 내 발달장애인(ASD) 대상 '안정 행동' 코치다.
        현재 발달장애인 근로자가 처한 상황에서 이 근로자를 안정시키기 위한 행동을 추천하라.
        **JSON 배열(문자열 3개)**만 출력하라. 다른 텍스트를 출력하지 마라.

        # INPUT
        <INPUT>
        발달장애인 근로자의 특성: %s
        상황: %s 중 %s
        감정: %s
        </INPUT>

        # RULES
        1) 독립 선택지 & 카테고리 다양성(순서 없음):
        - 세 행동은 서로 독립적인 선택지다(연속 수행 아님).
        - 세 행동 중 최소 2개 이상의 서로 다른 카테고리(E=환경, B=신체, C=인지)를 포함하라.
        - 이동/작업 상황이면 E 또는 B 중 하나를 반드시 포함하라.

        2) 각 행동은 도구/장소/숫자를 포함하도록 노력하라.
        - 도구는 소지·허용 시에만 제안하라(미소지/미허용 시 동일 카테고리 대체 동작 사용).

        3) 위험 금지 및 자동 치환(부분 일치 금지어 적용):
        - 다음 문자열이 포함되는 행동은 절대 추천하지 마라:
        "주머니에 손", "양손 주머니", "양손으로 귀 막", "눈 감고 (걷|서|이동)",
        "통로에 서", "문 앞", "난간", "손 놓고", "빠르게 이동", "뛰기", "달리기",
        "휴대폰 보며 걷", "볼륨 크게", "ANC 최", "콘센트", "임의 조작", "칼", "끓는", "뜨거운",
        "지게차", "랙", "팔레트", "사다리", "세제", "분무", "모르는 사람 촬영", "타인 신체 만지",
        "바닥에 눕", "의자 위에 서"

        - 이동/작업 중에는 시야·청각·균형을 크게 차단하는 동작 금지(예: 양손 주머니, 양손 귀 막기, 눈 오래 감기, 큰 몸 흔들기).

        4) 문장 형식:
        - 동사구 명사형(어간+“기”)으로 끝낼 것(예: “심호흡하기”), 6~16자, 마침표·이모지 금지.
        - 숫자는 구체적으로(초/회/분).

        5) 도움 요청은 최후의 수단:
        - 다른 제안이 모두 불가능할 때만 3번째에 넣어라.

        6) 최종 안전 셀프-체크(출력 전 내부 점검):
        - 세 문장 모두에 금지어가 없는지 확인하고, 이동/작업 맥락이면 최소 하나가 E 또는 B인지 확인하라.


        # OUTPUT FORMAT
        ["문자열","문자열","문자열"]

        # NOW
        JSON 배열만 출력하라.
        """.formatted(
                characteristic,
                schedule,
                condition,
                emotion
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

        boolean useRagForSteps = false;
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