package com.example.todak_server.dto;

import com.example.todak_server.entity.UserSetting;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingResponse {
    private Integer eventAlarmMinutes;
    private Integer emotionIntervalMinutes;
    private LocalTime emotionActiveStart;
    private LocalTime emotionActiveEnd;

    public static UserSettingResponse from(UserSetting s) {
        return UserSettingResponse.builder()
                .eventAlarmMinutes(s.getEventAlarmMinutes())
                .emotionIntervalMinutes(s.getEmotionIntervalMinutes())
                .emotionActiveStart(s.getEmotionActiveStart())
                .emotionActiveEnd(s.getEmotionActiveEnd())
                .build();
    }
}
