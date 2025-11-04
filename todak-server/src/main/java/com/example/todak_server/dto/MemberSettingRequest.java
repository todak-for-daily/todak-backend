package com.example.todak_server.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSettingRequest {
    private Integer eventAlarmMinutes;
    private Integer emotionIntervalMinutes;
    private LocalTime emotionActiveStart;
    private LocalTime emotionActiveEnd;
}
