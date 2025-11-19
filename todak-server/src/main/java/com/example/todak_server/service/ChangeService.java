package com.example.todak_server.service;

import com.example.todak_server.entity.ChangeLog;
import com.example.todak_server.entity.ChangeRead;
import com.example.todak_server.repository.ChangeLogRepository;
import com.example.todak_server.repository.ChangeReadRepository;
import com.example.todak_server.dto.response.ChangeLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeService {

    private final ChangeLogRepository changeLogRepo;
    private final ChangeReadRepository changeReadRepo;

    // 변경된 사항 감지 후 로그 저장
    public void compareAndLog(Long memberId, String category,
                              String field, String oldV, String newV) {

        if (!Objects.equals(oldV, newV)) {

            ChangeLog log = changeLogRepo.save(
                    ChangeLog.builder()
                            .memberId(memberId)
                            .category(category)
                            .fieldName(field)
                            .oldValue(oldV)
                            .newValue(newV)
                            .changedAt(LocalDateTime.now())
                            .build()
            );

            changeReadRepo.save(
                    ChangeRead.builder()
                            .memberId(memberId)
                            .changeLogId(log.getId())
                            .isRead(false)
                            .notified(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    // 전체 변경사항 조회 (읽은 것 + 안 읽은 것)
    public List<ChangeLogResponse> getChanges(Long memberId) {

        // 직원의 읽기 상태 목록
        List<ChangeRead> readList = changeReadRepo.findByMemberId(memberId);

        List<Long> logIds = readList.stream()
                .map(ChangeRead::getChangeLogId)
                .toList();

        // 실제 ChangeLog 조회
        List<ChangeLog> logs = changeLogRepo.findAllById(logIds);

        // 읽음 여부 Map으로 구성
        Map<Long, Boolean> readStatusMap =
                readList.stream()
                        .collect(Collectors.toMap(
                                ChangeRead::getChangeLogId,
                                ChangeRead::isRead
                        ));

        return logs.stream()
                .map(log -> new ChangeLogResponse(
                        log.getId(),
                        log.getCategory(),
                        log.getFieldName(),
                        log.getOldValue(),
                        log.getNewValue(),
                        log.getChangedAt(),
                        readStatusMap.getOrDefault(log.getId(), false)   // 읽음 여부 포함
                ))
                .sorted(Comparator.comparing(ChangeLogResponse::changedAt).reversed())
                .toList();
    }

    // 안 읽은 변경사항로그 조회
    public List<ChangeLogResponse> getUnreadChanges(Long memberId) {

        // 안 읽은 상태인 ChangeRead만 조회
        List<ChangeRead> unreadList =
                changeReadRepo.findByMemberIdAndIsReadFalse(memberId);

        List<Long> ids = unreadList.stream()
                .map(ChangeRead::getChangeLogId)
                .toList();

        List<ChangeLog> logs = changeLogRepo.findAllById(ids);

        // 모두 isRead = false
        return logs.stream()
                .map(log -> new ChangeLogResponse(
                        log.getId(),
                        log.getCategory(),
                        log.getFieldName(),
                        log.getOldValue(),
                        log.getNewValue(),
                        log.getChangedAt(),
                        false
                ))
                .sorted(Comparator.comparing(ChangeLogResponse::changedAt).reversed())
                .toList();
    }

    // 읽음 처리
    public void markAsRead(List<Long> changeLogIds, Long memberId) {

        List<ChangeRead> list = changeReadRepo.findByMemberIdAndIsReadFalse(memberId);

        for (ChangeRead cr : list) {
            if (changeLogIds.contains(cr.getChangeLogId())) {
                cr.setRead(true);
                changeReadRepo.save(cr);
            }
        }
    }
}