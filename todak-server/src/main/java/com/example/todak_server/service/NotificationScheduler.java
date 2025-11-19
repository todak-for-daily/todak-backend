package com.example.todak_server.service;

import com.example.todak_server.entity.CautionRead;
import com.example.todak_server.repository.CautionReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final CautionReadRepository cautionReadRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 0 * * * *") // 매 정각
    public void sendUnreadNotifications() {

        List<CautionRead> unread = cautionReadRepository.findByIsReadFalseAndNotifiedFalse();

        for (CautionRead cr : unread) {

            LocalDateTime deadline = cr.getCaution()
                    .getCreatedAt()
                    .plusHours(cr.getCaution().getNotifyAfterHours());

            if (LocalDateTime.now().isAfter(deadline)) {

                String token = fcmService.getMemberToken(cr.getMemberId());

                fcmService.sendNotification(
                        token,
                        "[필독] 안전 주의사항 미확인",
                        cr.getCaution().getTitle() + "을(를) 아직 읽지 않았어요!"
                );

                cr.setNotified(true);
                cautionReadRepository.save(cr);
            }
        }
    }
}
