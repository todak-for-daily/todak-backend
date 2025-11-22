package com.example.todak_server.controller;

import com.example.todak_server.dto.request.WorkScheduleCreateRequest;
import com.example.todak_server.dto.request.WorkScheduleUpdateRequest;
import com.example.todak_server.dto.response.WorkScheduleDetailResponse;
import com.example.todak_server.dto.response.WorkScheduleSimpleResponse;
import com.example.todak_server.service.WorkScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "근무 스케줄 API", description = "근무 스케줄 등록/수정/조회/삭제 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members/{memberId}/schedules")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    // 스케줄 등록
    @Operation(summary = "근무 스케줄 등록", description = "관리자가 특정 직원에게 스케줄을 등록함. 이미지 파일 업로드를 지원함.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WorkScheduleDetailResponse> create(
            @Parameter(description = "스케줄 등록할 직원 ID")
            @PathVariable("memberId") Long memberId,

            @Parameter(description = "스케줄 정보(JSON)")
            @RequestPart("data") WorkScheduleCreateRequest request,

            @Parameter(description = "작업환경 이미지 (선택)")
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        return ResponseEntity.ok(
                workScheduleService.create(memberId, request, imageFile)
        );
    }

    // 스케줄 수정
    @Operation(summary = "근무 스케줄 수정", description = "직원의 기존 근무 스케줄 정보를 수정함. 이미지도 교체 가능함.")
    @PutMapping(value = "/{scheduleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WorkScheduleDetailResponse> update(
            @Parameter(description = "스케줄 수정할 직원 ID")
            @PathVariable("memberId") Long memberId,

            @Parameter(description = "스케줄 ID")
            @PathVariable("scheduleId") Long scheduleId,

            @Parameter(description = "수정할 스케줄 데이터(JSON)")
            @RequestPart("data") WorkScheduleUpdateRequest request,

            @Parameter(description = "새로운 이미지 파일 (선택)")
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        return ResponseEntity.ok(
                workScheduleService.update(memberId, scheduleId, request, imageFile)
        );
    }

    // 월별 스케줄 조회
    @Operation(summary = "월별 스케줄 조회", description = "직원의 특정 연/월에 해당하는 근무 스케줄을 조회함.")
    @GetMapping
    public ResponseEntity<List<WorkScheduleSimpleResponse>> getMonthly(
            @Parameter(description = "멤버 ID") @PathVariable("memberId") Long memberId,
            @Parameter(description = "연도") @RequestParam("year") int year,
            @Parameter(description = "월") @RequestParam("month") int month
    ) {
        return ResponseEntity.ok(
                workScheduleService.getMonthly(memberId, year, month)
        );
    }

    // 스케줄 삭제
    @Operation(summary = "근무 스케줄 삭제", description = "스케줄 ID에 해당하는 근무 스케줄을 삭제함.")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "스케줄 수정할 직원 ID") @PathVariable("memberId") Long memberId,
            @Parameter(description = "수정할 스케줄 ID") @PathVariable("scheduleId") Long scheduleId
    ) {
        workScheduleService.delete(memberId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
