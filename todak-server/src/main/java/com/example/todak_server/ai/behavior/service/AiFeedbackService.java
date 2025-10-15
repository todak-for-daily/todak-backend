package com.example.todak_server.ai.behavior.service;

import com.example.todak_server.entity.AiFeedbackRecord;
import com.example.todak_server.repository.AiFeedbackRepository;
import com.example.todak_server.ai.behavior.service.AiSessionContextService;
import com.example.todak_server.service.EmotionCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiFeedbackService {

    private final AiFeedbackRepository aiFeedbackRepository;
    private final AiSessionContextService aiSessionContextService;
    private final EmotionCardService emotionCardService;

    public void saveFeedback(Long memberId, String afterEmotion) {
        var context = aiSessionContextService.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("AI 세션 정보가 없습니다."));

        // 감정 단계 비교 (emotion_cards.json 기준)
        int before = emotionCardService.getLevel(context.getEmotionCard());
        int after = emotionCardService.getLevel(afterEmotion);
        int score = after - before;

        AiFeedbackRecord record = new AiFeedbackRecord();
        record.setMemberId(memberId);
        record.setEmotionCard(context.getEmotionCard());
        record.setSituationCardId(context.getSituationCardId());
        record.setSelectedAction(context.getSelectedAction());
        record.setBeforeEmotion(context.getEmotionCard());
        record.setAfterEmotion(afterEmotion);
        record.setEmotionChangeScore(score);

        aiFeedbackRepository.save(record);

        // 피드백 저장 후 세션 정리
        aiSessionContextService.deleteByMemberId(memberId);
    }
}
