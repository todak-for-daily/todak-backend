package com.example.todak_server.ai.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        // Gemini 형식 바디
        String prompt = makePrompt(payload);
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", prompt))
                        )
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> res = response.getBody();
        if (res == null || !res.containsKey("candidates")) {
            return List.of("추천 결과 없음");
        }

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
        List<String> habits = (List<String>) payload.get("behavior_habits");
        String schedule = (String) payload.get("current_schedule");

        return String.format("""
            사용자의 감정은 '%s'이고, 상황은 '%s'입니다.
            사용자의 습관은 %s이며, 일정은 '%s'입니다.
            이 상황에서 할 수 있는 간단한 행동 3가지를 JSON 배열로만 반환해주세요.
            예시: ["심호흡하기", "산책하기", "조용한 곳으로 이동하기"]
            """, emotion, situation.get("text"), habits, schedule);
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

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
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



}
