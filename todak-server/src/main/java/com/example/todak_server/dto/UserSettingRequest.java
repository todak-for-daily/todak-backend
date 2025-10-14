package com.example.todak_server.dto;

import com.example.todak_server.entity.UserSetting;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingRequest {
    private Integer eventAlarmMinutes;
    private Integer emotionIntervalMinutes;
    private LocalTime emotionActiveStart;
    private LocalTime emotionActiveEnd;
}
