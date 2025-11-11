package com.example.todak_server.controller;

import com.example.todak_server.dto.request.GeneralScheduleRequestDto;
import com.example.todak_server.dto.request.WeeklyScheduleRequestDto;
import com.example.todak_server.dto.response.GeneralScheduleResponseDto;
import com.example.todak_server.dto.response.WeeklyScheduleResponseDto;
import com.example.todak_server.entity.*;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name="기능1: 일정 관리", description="주간 루틴(weekly) 및 일반 일정(general) 관리 API")
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final MemberRepository memberRepository;
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService, MemberRepository memberRepository) {
        this.scheduleService = scheduleService;
        this.memberRepository = memberRepository;
    }

    // Weekly 일정 생성 + General 스냅샷 생성
    @Operation(
            summary = "주간 일정 등록",
            description = "weekly 일정 등록 후, 오늘부터 8주 후까지의 일반 일정(GENERAL)을 자동 생성."
    )
    @PostMapping("/weekly")
    public WeeklyScheduleResponseDto createWeeklySchedule(
            @RequestBody WeeklyScheduleRequestDto dto,
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        WeeklySchedule weekly = new WeeklySchedule();
        weekly.setMember(member);
        weekly.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
        weekly.setStartTime(dto.startTime());
        weekly.setEndTime(dto.endTime());
        weekly.setTitle(dto.title());
        weekly.setColor(dto.color());
        weekly.setLocation(dto.location());

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
                saved.getColor(),
                saved.getLocation()
        );
    }

    // 특정 멤버의 Weekly 일정 특정 요일 or 전체 조회 (day 없으면 전체 조회)
    @Operation(
            summary = "주간 일정 조회",
            description = "특정 요일(day) 또는 전체 주간 일정 목록을 조회.. day 미지정 시 전체가 반환됨."
    )
    @GetMapping("/weekly")
    public List<WeeklyScheduleResponseDto> getWeeklySchedules(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @Parameter(description = "요일(예: MONDAY). 미지정 시 전체 조회", required = false, example = "MONDAY")
            @RequestParam(value = "day", required = false) String day
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        var list = (day == null)
                ? scheduleService.getWeeklySchedule(member)
                : scheduleService.getWeeklySchedule(
                member,
                com.example.todak_server.entity.DayOfWeek.valueOf(day.toUpperCase())
        );

        return list.stream()
                .map(w -> new WeeklyScheduleResponseDto(
                        w.getId(),
                        w.getDayOfWeek().name(),
                        w.getStartTime(),
                        w.getEndTime(),
                        w.getTitle(),
                        w.getColor(),
                        w.getLocation()
                ))
                .toList();
    }

    // Weekly 일정 수정
    @Operation(
            summary = "주간 일정 수정",
            description = "ID에 해당하는 주간 일정을 수정."
    )
    @PutMapping("/weekly/{id}")
    public WeeklyScheduleResponseDto updateWeekly(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestBody WeeklyScheduleRequestDto dto
    ) {
        Member member = new Member();
        member.setId(memberId);

        WeeklySchedule updated = new WeeklySchedule();
        updated.setMember(member);
        updated.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
        updated.setStartTime(dto.startTime());
        updated.setEndTime(dto.endTime());
        updated.setTitle(dto.title());
        updated.setColor(dto.color());
        updated.setLocation(dto.location());

        WeeklySchedule saved = scheduleService.updateWeeklySchedule(id, updated);

        return new WeeklyScheduleResponseDto(
                saved.getId(),
                saved.getDayOfWeek().name(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor(),
                saved.getLocation()
        );
    }

    // Weekly 일정 삭제
    @Operation(
            summary = "주간 일정 삭제",
            description = "ID에 해당하는 주간 일정을 삭제."
    )
    @DeleteMapping("/weekly/{id}")
    public void deleteWeekly(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        scheduleService.deleteWeeklySchedule(memberId, id);
    }

    // 특정 멤버의 General 일정 조회 (기간별)
    @Operation(
            summary = "일반 일정 조회",
            description = "기간(startDate ~ endDate) 내의 일반 일정 목록을 조회. 날짜 형식은 yyyy-MM-dd"
    )
    @GetMapping("/general")
    public List<GeneralScheduleResponseDto> getGeneralSchedules(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @Parameter(description = "조회 시작일 (yyyy-MM-dd)", required = true, example = "2025-01-01")
            @RequestParam LocalDate startDate,
            @Parameter(description = "조회 종료일 (yyyy-MM-dd)", required = true, example = "2025-01-31")
            @RequestParam LocalDate endDate
    ) {
        Member member = new Member();
        member.setId(memberId);

        return scheduleService.getGeneralSchedule(member, startDate, endDate).stream()
                .map(g -> new GeneralScheduleResponseDto(
                        g.getId(),
                        g.getDate(),
                        g.getStartTime(),
                        g.getEndTime(),
                        g.getTitle(),
                        g.getColor(),
                        g.getLocation()
                ))
                .toList();
    }

    // General 일정 단일 생성
    @Operation(
            summary = "일반 일정 등록",
            description = "사용자가 직접 단일 General 일정을 등록"
    )
    @PostMapping("/general")
    public GeneralScheduleResponseDto createGeneral(
            @RequestBody GeneralScheduleRequestDto dto,
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        GeneralSchedule general = new GeneralSchedule();
        general.setMember(member);
        general.setDate(dto.date());
        general.setStartTime(dto.startTime());
        general.setEndTime(dto.endTime());
        general.setTitle(dto.title());
        general.setColor(dto.color());
        general.setLocation(dto.location());

        GeneralSchedule saved = scheduleService.createGeneralSchedule(general);

        return new GeneralScheduleResponseDto(
                saved.getId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor(),
                saved.getLocation()
        );
    }

    // General 일정 수정
    @Operation(
            summary = "일반 일정 수정",
            description = "ID에 해당하는 일반 일정을 수정."
    )
    @PutMapping("/general/{id}")
    public GeneralScheduleResponseDto updateGeneral(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestBody GeneralScheduleRequestDto dto,
            @PathVariable Long id
    ) {
        Member member = new Member();
        member.setId(memberId);

        GeneralSchedule updated = new GeneralSchedule();
        updated.setMember(member);
        updated.setDate(dto.date());
        updated.setStartTime(dto.startTime());
        updated.setEndTime(dto.endTime());
        updated.setTitle(dto.title());
        updated.setColor(dto.color());
        updated.setLocation(dto.location());

        GeneralSchedule saved = scheduleService.updateGeneralSchedule(id, updated);

        return new GeneralScheduleResponseDto(
                saved.getId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getTitle(),
                saved.getColor(),
                saved.getLocation()
        );
    }

    // General 일정 삭제 (soft delete)
    @Operation(
            summary = "일반 일정 삭제",
            description = "ID에 해당하는 일반 일정을 삭제. (Soft delete)"
    )
    @DeleteMapping("/general/{id}")
    public void deleteGeneral(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        scheduleService.deleteGeneralSchedule(memberId, id);
    }
}
