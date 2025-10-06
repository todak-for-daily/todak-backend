package com.example.todak_server.controller;

import com.example.todak_server.dto.request.GeneralScheduleRequestDto;
import com.example.todak_server.dto.request.WeeklyScheduleRequestDto;
import com.example.todak_server.dto.response.GeneralScheduleResponseDto;
import com.example.todak_server.dto.response.WeeklyScheduleResponseDto;
import com.example.todak_server.entity.*;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final MemberRepository memberRepository;

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService,  MemberRepository memberRepository) {
        this.scheduleService = scheduleService;
        this.memberRepository = memberRepository;
    }


    // Weekly 일정 생성 + General 스냅샷 생성
    @PostMapping("/weekly")
    public WeeklyScheduleResponseDto createWeeklySchedule(@RequestBody WeeklyScheduleRequestDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        WeeklySchedule weekly = new WeeklySchedule();
        weekly.setMember(member);
        weekly.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
        weekly.setStartTime(dto.startTime());
        weekly.setEndTime(dto.endTime());
        weekly.setTitle(dto.title());
        weekly.setColor(dto.color());

        // 기본 범위: 오늘부터 8주 후까지
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(8);

        WeeklySchedule saved = scheduleService.createWeeklySchedule(weekly, start, end);

        return new WeeklyScheduleResponseDto(
                saved.getId(),
                saved.getDayOfWeek().name(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor()
        );
    }

    // 특정 멤버의 Weekly 일정 전체 조회
    @GetMapping("/weekly/{memberId}")
    public List<WeeklyScheduleResponseDto> getWeeklySchedules(@PathVariable Long memberId) {
        Member member = new Member();
        member.setId(memberId);

        return scheduleService.getWeeklySchedule(member).stream()
                .map(w -> new WeeklyScheduleResponseDto(
                        w.getId(),
                        w.getDayOfWeek().name(),
                        w.getStartTime(),
                        w.getEndTime(),
                        w.getTitle(),
                        w.getColor()
                ))
                .toList();
    }

    // 특정 멤버의 General 일정 조회 (기간별)
    @GetMapping("/general/{memberId}")
    public List<GeneralScheduleResponseDto> getGeneralSchedules(@PathVariable Long memberId,
                                                                @RequestParam LocalDate startDate,
                                                                @RequestParam LocalDate endDate) {
        Member member = new Member();
        member.setId(memberId);

        return scheduleService.getGeneralSchedule(member, startDate, endDate).stream()
                .map(g -> new GeneralScheduleResponseDto(
                        g.getId(),
                        g.getDate(),
                        g.getStartTime(),
                        g.getEndTime(),
                        g.getTitle(),
                        g.getColor()
                ))
                .toList();
    }

    // General 일정 단일 생성
    @PostMapping("/general")
    public GeneralScheduleResponseDto createGeneral(@RequestBody GeneralScheduleRequestDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        GeneralSchedule general = new GeneralSchedule();
        general.setMember(member);
        general.setDate(dto.date());
        general.setStartTime(dto.startTime());
        general.setEndTime(dto.endTime());
        general.setTitle(dto.title());
        general.setColor(dto.color());

        GeneralSchedule saved = scheduleService.createGeneralSchedule(general);

        return new GeneralScheduleResponseDto(
                saved.getId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor()
        );
    }

    // General 일정 수정
    @PutMapping("/general/{id}")
    public GeneralScheduleResponseDto updateGeneral(@PathVariable Long id,
                                                    @RequestBody GeneralScheduleRequestDto dto) {
        Member member = new Member();
        member.setId(dto.memberId());

        GeneralSchedule updated = new GeneralSchedule();
        updated.setMember(member);
        updated.setDate(dto.date());
        updated.setStartTime(dto.startTime());
        updated.setEndTime(dto.endTime());
        updated.setTitle(dto.title());
        updated.setColor(dto.color());

        GeneralSchedule saved = scheduleService.updateGeneralSchedule(id, updated);

        return new GeneralScheduleResponseDto(
                saved.getId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor()
        );
    }

    // General 일정 삭제 (soft delete)
    @DeleteMapping("/general/{id}")
    public void deleteGeneral(@PathVariable Long id) {
        scheduleService.deleteGeneralSchedule(id);
    }

    // Weekly 일정 수정
    @PutMapping("/weekly/{id}")
    public WeeklyScheduleResponseDto updateWeekly(@PathVariable Long id,
                                                  @RequestBody WeeklyScheduleRequestDto dto) {
        Member member = new Member();
        member.setId(dto.memberId());

        WeeklySchedule updated = new WeeklySchedule();
        updated.setMember(member);
        updated.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
        updated.setStartTime(dto.startTime());
        updated.setEndTime(dto.endTime());
        updated.setTitle(dto.title());
        updated.setColor(dto.color());

        WeeklySchedule saved = scheduleService.updateWeeklySchedule(id, updated);

        return new WeeklyScheduleResponseDto(
                saved.getId(),
                saved.getDayOfWeek().name(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor()
        );
    }

    // Weekly 일정 삭제
    @DeleteMapping("/weekly/{id}")
    public void deleteWeekly(@PathVariable Long id) {
        scheduleService.deleteWeeklySchedule(id);
    }

}
