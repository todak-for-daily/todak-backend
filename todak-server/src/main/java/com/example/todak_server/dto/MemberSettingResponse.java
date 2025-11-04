package com.example.todak_server.dto;

import com.example.todak_server.entity.MemberSetting;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSettingResponse {
    private Integer eventAlarmMinutes;
    private Integer emotionIntervalMinutes;
    private LocalTime emotionActiveStart;
    private LocalTime emotionActiveEnd;

    public static MemberSettingResponse from(MemberSetting s) {
        return MemberSettingResponse.builder()
                .eventAlarmMinutes(s.getEventAlarmMinutes())
                .emotionIntervalMinutes(s.getEmotionIntervalMinutes())
                .emotionActiveStart(s.getEmotionActiveStart())
                .emotionActiveEnd(s.getEmotionActiveEnd())
                .build();
    }
}
