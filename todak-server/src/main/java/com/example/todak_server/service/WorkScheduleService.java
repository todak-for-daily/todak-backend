package com.example.todak_server.service;

import com.example.todak_server.dto.request.WorkScheduleCreateRequest;
import com.example.todak_server.dto.request.WorkScheduleUpdateRequest;
import com.example.todak_server.dto.response.WorkScheduleDetailResponse;
import com.example.todak_server.dto.response.WorkScheduleSimpleResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.WorkSchedule;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.repository.WorkScheduleRepository;
import com.example.todak_server.util.GcsUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final MemberRepository memberRepository;
    private final GcsUploader gcsUploader;

    // 스케줄 등록
    public WorkScheduleDetailResponse create(
            Long memberId,
            WorkScheduleCreateRequest req,
            MultipartFile imageFile
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("직원이 존재하지 않습니다."));

        String imgUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imgUrl = gcsUploader.upload(imageFile, "schedule");
        }

        WorkSchedule schedule = new WorkSchedule();
        schedule.setMember(member);
        schedule.setDate(req.date());
        schedule.setStartTime(req.startTime());
        schedule.setEndTime(req.endTime());
        schedule.setDescription(req.description());
        schedule.setImgUrl(imgUrl);

        workScheduleRepository.save(schedule);

        return toDetailResponse(schedule);
    }

    // 스케줄 수정
    public WorkScheduleDetailResponse update(
            Long memberId,
            Long scheduleId,
            WorkScheduleUpdateRequest req,
            MultipartFile imageFile
    ) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 스케줄을 찾을 수 없습니다."));

        schedule.setDate(req.date());
        schedule.setStartTime(req.startTime());
        schedule.setEndTime(req.endTime());
        schedule.setDescription(req.description());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imgUrl = gcsUploader.upload(imageFile, "schedule");
            schedule.setImgUrl(imgUrl);
        }

        workScheduleRepository.save(schedule);
        return toDetailResponse(schedule);
    }

    // 월별 스케줄 조회
    public List<WorkScheduleSimpleResponse> getMonthly(Long memberId, int year, int month) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("직원 없음"));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return workScheduleRepository.findByMemberAndDateBetween(member, start, end)
                .stream()
                .map(this::toSimpleResponse)
                .toList();
    }

    private WorkScheduleDetailResponse toDetailResponse(WorkSchedule s) {
        return new WorkScheduleDetailResponse(
                s.getId(),
                s.getMember().getId(),
                s.getMember().getNickname(),
                s.getDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getDescription(),
                s.getImgUrl(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

    private WorkScheduleSimpleResponse toSimpleResponse(WorkSchedule s) {
        return new WorkScheduleSimpleResponse(
                s.getId(),
                s.getDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getImgUrl()
        );
    }

    // 스케줄 삭제
    public void delete(Long memberId, Long scheduleId) {

        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("스케줄을 찾을 수 없습니다."));

        // 이미지 삭제
         if (schedule.getImgUrl() != null) {
             gcsUploader.delete(schedule.getImgUrl());
         }

        workScheduleRepository.delete(schedule);
    }
}
