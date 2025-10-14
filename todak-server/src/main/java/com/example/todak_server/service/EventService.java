package com.example.todak_server.service;

import com.example.todak_server.dto.EventResponse;
import com.example.todak_server.entity.Event;
import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.UserSetting;
import com.example.todak_server.repository.EventRepository;
import com.example.todak_server.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserSettingRepository userSettingRepository;

    public List<EventResponse> getTodayEvents(Member member) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Event> events = eventRepository.findByMemberAndStartTimeBetween(member, startOfDay, endOfDay);
        int preAlarm = userSettingRepository.findByMember(member)
                .map(UserSetting::getEventAlarmMinutes)
                .orElse(30);

        return events.stream()
                .map(e -> EventResponse.from(e, preAlarm))
                .collect(Collectors.toList());
    }
}
