package com.example.todak_server.controller;

import com.example.todak_server.dto.request.ChangeReadRequest;
import com.example.todak_server.dto.response.ChangeLogResponse;
import com.example.todak_server.service.ChangeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "변경사항 API", description = "변경된 내용 조회 및 읽음 처리 기능 API")
@RestController
@RequestMapping("/api/changes")
@RequiredArgsConstructor
public class ChangeController {

    private final ChangeService changeService;

    @Operation(summary = "변경사항 조회", description = "해당 직원에게 발생한 변경사항 목록을 조회함.")
    @GetMapping
    public List<ChangeLogResponse> getChanges(
            @RequestParam Long memberId
    ) {
        return changeService.getChanges(memberId);
    }

    @Operation(summary = "안 읽은 변경사항 조회", description = "직원이 아직 읽지 않은 변경사항 목록만 조회함. (빨간 글씨 강조용)")
    @GetMapping("/unread")
    public List<ChangeLogResponse> getUnread(@RequestParam Long memberId) {
        return changeService.getUnreadChanges(memberId);
    }

    @Operation(summary = "변경사항 읽음 처리", description = "직원이 변경사항을 읽었음을 표시함.")
    @PostMapping("/read")
    public String markAsRead(
            @RequestBody ChangeReadRequest req
    ) {
        changeService.markAsRead(req.changeLogIds(), req.memberId());
        return "OK";
    }
}
