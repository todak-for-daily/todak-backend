package com.example.todak_server.service;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.MemberSetting;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.repository.MemberSettingRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@DependsOn("firebaseApp")
@RequiredArgsConstructor
public class FcmService {

    private final MemberSettingRepository memberSettingRepository;
    private final MemberRepository memberRepository;

    private ThreadPoolTaskScheduler scheduler;

    private void initScheduler() {
        if (scheduler == null) {
            scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(10);
            scheduler.setThreadNamePrefix("fcm-push-");
            scheduler.initialize();
        }
    }

    public void registerUserSchedule(Long memberId) {
        initScheduler();

        MemberSetting setting = memberSettingRepository.findByMemberId(memberId)
                .orElse(null);

        if (setting == null) {
            System.out.println("[FCM] No setting found for memberId=" + memberId);
            return;
        }

        try {
            scheduleEmotionPush(setting);
        } catch (Exception e) {
            System.err.println("[FCM] schedule failed for memberId=" + memberId + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 사용자별 푸시 스케줄 등록
    public void scheduleEmotionPush(MemberSetting setting) {
        initScheduler();

        if (setting == null || setting.getMember() == null) return;

        Member member = setting.getMember();

        // 1) FCM 토큰 체크
        if (member.getFcmToken() == null || member.getFcmToken().isBlank()) {
            System.out.println("[" + member.getNickname() + "] has no fcm token, skipping");
            return;
        }

        // 2) 필수 값 검증
        LocalTime start = setting.getEmotionActiveStart();
        LocalTime end   = setting.getEmotionActiveEnd();
        Integer intervalMinutes = setting.getEmotionIntervalMinutes();

        if (start == null || end == null || intervalMinutes == null || intervalMinutes <= 0) {
            System.out.println("[" + member.getNickname() + "] invalid FCM schedule config, skipping"
                    + " (start=" + start + ", end=" + end + ", interval=" + intervalMinutes + ")");
            return;
        }

        // 3) 첫 실행 시각 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstRun = now.withHour(start.getHour())
                .withMinute(start.getMinute())
                .withSecond(0)
                .withNano(0);
        if (firstRun.isBefore(now)) firstRun = firstRun.plusDays(1);

        long delayMs = Duration.between(now, firstRun).toMillis();
        long periodMs = intervalMinutes * 60_000L;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalTime nowTime = LocalTime.now();
                if (!isWithin(nowTime, start, end)) return;
                sendEmotionPush(member);
            } catch (Exception e) {
                System.err.println("Failed push [" + member.getNickname() + "]: " + e.getMessage());
                e.printStackTrace();
            }
        }, new Date(System.currentTimeMillis() + delayMs), periodMs);

        System.out.println("Emotion push scheduled for " + member.getNickname() +
                " every " + intervalMinutes + " min (" + start + " ~ " + end + ")");
    }

    // 시간 구간 체크
    private boolean isWithin(LocalTime now, LocalTime start, LocalTime end) {
        if (start.equals(end)) return true;
        if (start.isBefore(end)) {
            return !now.isBefore(start) && !now.isAfter(end);
        } else {
            return !now.isBefore(start) || !now.isAfter(end);
        }
    }

    // 푸시 전송 로직
    public void sendEmotionPush(Member member) {
        try {
            Notification notification = Notification.builder()
                    .setTitle("오늘 기분이 어때요? 😊")
                    .setBody("지금 기분을 확인해볼까요?")
                    .build();

            Message message = Message.builder()
                    .setToken(member.getFcmToken())
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM sent to " + member.getNickname() + ": " + response);

        } catch (Exception e) {
            System.err.println("Failed to send push to " + member.getNickname());
            e.printStackTrace();
        }
    }

    public String getMemberToken(Long memberId) {
        return memberRepository.findById(memberId)
                .map(Member::getFcmToken)
                .orElse(null);
    }

    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isBlank()) return;

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM sent: " + response);

        } catch (Exception e) {
            System.err.println("FCM send failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
