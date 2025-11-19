package com.example.todak_server.service;

import com.example.todak_server.dto.response.NotificationResponse;
import com.example.todak_server.entity.Notification;
import com.example.todak_server.entity.NotificationRead;
import com.example.todak_server.repository.NotificationReadRepository;
import com.example.todak_server.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final NotificationReadRepository notificationReadRepo;
    private final FcmService fcmService;

    // 공지 전송 + DB 기록
    @Transactional
    public void sendNotice(Long adminId, String title, String body, List<Long> targetMemberIds) {

        Notification noti = notificationRepo.save(
                Notification.builder()
                        .adminId(adminId)
                        .title(title)
                        .body(body)
                        .category("NOTICE")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        for (Long memberId : targetMemberIds) {

            notificationReadRepo.save(
                    NotificationRead.builder()
                            .notification(noti)
                            .memberId(memberId)
                            .isRead(false)
                            .build()
            );

            String token = fcmService.getMemberToken(memberId);
            if (token != null) {
                fcmService.sendNotification(token, title, body);
            }
        }
    }

    // 사용자 읽음 처리
    public List<NotificationResponse> getMemberNotifications(Long memberId) {

        return notificationReadRepo.findByMemberIdOrderByNotificationCreatedAtDesc(memberId)
                .stream()
                .map(nr -> new NotificationResponse(
                        nr.getNotification().getId(),
                        nr.getNotification().getTitle(),
                        nr.getNotification().getBody(),
                        nr.getNotification().getCategory(),
                        nr.isRead(),
                        nr.getNotification().getCreatedAt()
                ))
                .toList();

    }

    // 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {

        notificationReadRepo.findByIsReadFalseAndMemberId(memberId)
                .stream()
                .filter(nr -> nr.getNotification().getId().equals(notificationId))
                .findFirst()
                .ifPresent(nr -> {
                    nr.setRead(true);
                    nr.setReadAt(LocalDateTime.now());
                    notificationReadRepo.save(nr);
                });
    }
}
