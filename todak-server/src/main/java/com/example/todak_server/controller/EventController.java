package com.example.todak_server.controller;

import com.example.todak_server.dto.EventResponse;
import com.example.todak_server.entity.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.todak_server.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/today")
    public ResponseEntity<List<EventResponse>> getTodayEvents(@AuthenticationPrincipal Member member) {
        List<EventResponse> events = eventService.getTodayEvents(member);
        return ResponseEntity.ok(events);
    }
}
