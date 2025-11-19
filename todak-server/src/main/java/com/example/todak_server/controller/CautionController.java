package com.example.todak_server.controller;

import com.example.todak_server.dto.request.*;
import com.example.todak_server.dto.response.CautionResponse;
import com.example.todak_server.service.CautionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "주의사항 및 작업 환경 API", description = "근무 환경 이미지/영상 업로드, 주의사항 등록/조회/읽음 기능 API")
@RestController
@RequestMapping("/api/cautions")
@RequiredArgsConstructor
public class CautionController {

    private final CautionService cautionService;

    @Operation(summary = "주의사항 등록", description = "관리자가 필독 주의사항을 등록하고 이미지/영상을 업로드함.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CautionResponse createCaution(
            @RequestPart("data") CautionCreateRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        return cautionService.create(request, imageFile);
    }

    @Operation(summary = "주의사항 조회", description = "해당 직원에게 배정된 전체 주의사항을 조회함.")
    @GetMapping
    public List<CautionResponse> getList(
            @RequestParam Long memberId
    ) {
        return cautionService.getList(memberId);
    }

    @Operation(summary = "읽음 처리", description = "직원이 특정 주의사항을 확인했음을 표시함.")
    @PostMapping("/read")
    public String markAsRead(
            @RequestBody CautionReadRequest req
    ) {
        cautionService.markAsRead(req.memberId(), req.cautionId());
        return "OK";
    }
}
