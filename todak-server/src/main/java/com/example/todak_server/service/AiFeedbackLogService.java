package com.example.todak_server.service;

import com.example.todak_server.dto.response.AiFeedbackLogResponse;
import com.example.todak_server.entity.AiFeedbackRecord;
import com.example.todak_server.repository.AiFeedbackRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiFeedbackLogService {

    private final AiFeedbackRecordRepository aiFeedbackRecordRepository;

    public List<AiFeedbackLogResponse> getLogs(Long memberId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDateTime.MIN;

        LocalDateTime end = (endDate != null)
                ? endDate.atTime(23, 59, 59)
                : LocalDateTime.MAX;

        List<AiFeedbackRecord> records;

        if (startDate != null && endDate != null) {
            records = aiFeedbackRecordRepository
                    .findByMemberIdAndCreatedAtBetweenOrderByCreatedAtDesc(memberId, start, end);
        } else {
            records = aiFeedbackRecordRepository
                    .findByMemberIdOrderByCreatedAtDesc(memberId);
        }

        return records.stream()
                .map(r -> new AiFeedbackLogResponse(
                        r.getId(),
                        r.getEmotionCard(),
                        r.getSituationCardId(),
                        r.getSelectedAction(),
                        r.getBeforeEmotion(),
                        r.getAfterEmotion(),
                        r.getEmotionChangeScore(),
                        r.getCreatedAt()
                ))
                .toList();
    }
}
