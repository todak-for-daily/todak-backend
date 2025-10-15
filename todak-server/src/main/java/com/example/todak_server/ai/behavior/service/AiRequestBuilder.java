package com.example.todak_server.ai.behavior.service;

import com.example.todak_server.ai.behavior.dto.request.AiRecommendRequest;
import com.example.todak_server.service.SituationCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// 감정카드, 상황카드, 행동 리스트, 현재 일정을 하나의 json으로 묶어줌.
@Component
@RequiredArgsConstructor
public class AiRequestBuilder {

    private final SituationCardService situationCardService;

    public Map<String, Object> build(AiRecommendRequest dto, List<String> habits, String schedule) {
        String situationText = situationCardService.getSituationText(dto.situationCardId());

        Map<String, Object> situationCard = Map.of(
                "id", dto.situationCardId(),
                "text", situationText
        );

        return Map.of(
                "situation_card", situationCard,
                "behavior_habits", habits,
                "current_schedule", schedule
        );
    }
}
