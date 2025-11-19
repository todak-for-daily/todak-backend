package com.example.todak_server.scheduler;

import com.example.todak_server.entity.CautionRead;
import com.example.todak_server.repository.CautionReadRepository;
import com.example.todak_server.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CautionScheduler {

    private final CautionReadRepository cautionReadRepository;
    private final FcmService fcmService;

    // 30분마다 검사
    @Transactional
    @Scheduled(cron = "0 */30 * * * *")
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
                        "아직 확인하지 않은 주의사항이 있습니다."
                );

                cr.setNotified(true);
                cautionReadRepository.save(cr);
            }
        }
    }
}
