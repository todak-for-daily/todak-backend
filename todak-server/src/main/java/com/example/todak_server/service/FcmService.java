package com.example.todak_server.service;

import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.MemberSetting;
import com.example.todak_server.repository.MemberSettingRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final MemberSettingRepository memberSettingRepository;
    private ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    public void init() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.initialize();

        // 서버 시작 시 모든 사용자들의 설정을 불러와서 스케줄 등록
        List<MemberSetting> settings = memberSettingRepository.findAll();
        settings.forEach(this::scheduleEmotionPush);
    }
    // 사용자별 푸시 스케줄 등록
    public void scheduleEmotionPush(MemberSetting setting) {
        Member member = setting.getMember();
        if (member.getFcmToken() == null) {
            System.out.println("[" + member.getNickname() + "]" + " has no fcm token, skipping");
            return;
        }

        LocalTime start = setting.getEmotionActiveStart();
        LocalTime end = setting.getEmotionActiveEnd();
        int intervalMinutes = setting.getEmotionIntervalMinutes();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstRun = now.withHour(start.getHour()).withMinute(start.getHour()).withSecond(0);
        if (firstRun.isBefore(now)) firstRun = firstRun.plusDays(1);

        long delay = Duration.between(now, firstRun).toMillis();

        scheduler.scheduleAtFixedRate(() -> {
            LocalTime nowTime = LocalTime.now();
            if (nowTime.isBefore(start) || nowTime.isAfter(end)) {
                return;  // 활성 구간 외에는 발송 안 함
            }
            sendEmotionPush(member);
        }, new Date(System.currentTimeMillis() + delay), intervalMinutes * 60 + 1000L);

        System.out.println("Emotion push schduled for " + member.getNickname() +
                " every " + intervalMinutes + " min (" + start + " ~ " + end + ")");
    }

    // FCM 전송 로직
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
}
