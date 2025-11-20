package com.example.todak_server.ai.behavior.service;

import com.example.todak_server.ai.behavior.dto.request.AiRecommendRequest;
import com.example.todak_server.service.SituationCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// 감정카드, 상황카드, 행동 리스트(상황:내용), 현재 일정을 하나의 json으로 묶어줌.
@Component
@RequiredArgsConstructor
public class AiRequestBuilder {

    private final SituationCardService situationCardService;

    public Map<String, Object> build(
            AiRecommendRequest dto,
            Map<String, String> situationHabits,
            Map<String, Object> scheduleContext,
            String currentTime
    ) {
        // 상황카드 전체 정보 조회 (id + category + text)
        Map<String, Object> card = situationCardService.getSituationById(dto.situationCardId());

        Map<String, Object> situationCard = Map.of(
                "id", card.get("id"),
                "category", card.get("category"),
                "text", card.get("text")
        );

        return Map.of(
                "situation_card", situationCard,
                "behavior_habits", situationHabits,
                "schedule_context", scheduleContext, // prev/current/next 이름들
                "current_time", currentTime           // ISO 문자열
        );
    }


}
