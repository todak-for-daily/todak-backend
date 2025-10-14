package com.example.todak_server.dto;

import com.example.todak_server.entity.Event;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String event;
    private LocalDateTime startTime;
    private LocalDateTime notifyAt;

    public static EventResponse from(Event e, int preAlarmMinutes) {
        LocalDateTime notifyTime = e.getStartTime().minusMinutes(preAlarmMinutes);
        return EventResponse.builder()
                .id(e.getId())
                .event(e.getEvent())
                .startTime(e.getStartTime())
                .notifyAt(notifyTime)
                .build();
    }
}
