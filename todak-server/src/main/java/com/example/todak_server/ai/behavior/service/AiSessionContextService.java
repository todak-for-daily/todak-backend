package com.example.todak_server.ai.behavior.service;

import com.example.todak_server.entity.AiSessionContext;
import com.example.todak_server.repository.AiSessionContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiSessionContextService {
    private final AiSessionContextRepository repository;

    public void saveOrUpdate(Long memberId, String emotionCard, String situationCardId, String selectedAction) {
        AiSessionContext context = repository.findByMemberId(memberId)
                .orElseGet(AiSessionContext::new);

        context.setMemberId(memberId);
        if (emotionCard != null) context.setEmotionCard(emotionCard);
        if (situationCardId != null) context.setSituationCardId(situationCardId);
        if (selectedAction != null) context.setSelectedAction(selectedAction);
        repository.save(context);
    }

    public Optional<AiSessionContext> findByMemberId(Long memberId) {
    return repository.findByMemberId(memberId);
    }

    @Transactional
    public void deleteByMemberId(Long memberId) {
        repository.deleteByMemberId(memberId);
    }
}
