package com.example.todak_server.controller;

import com.example.todak_server.dto.response.AiFeedbackLogResponse;
import com.example.todak_server.service.AiFeedbackLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members/{memberId}/logs")
public class AiFeedbackLogController {

    private final AiFeedbackLogService aiFeedbackLogService;

    @GetMapping
    public List<AiFeedbackLogResponse> getLogs(
            @PathVariable("memberId") Long memberId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return aiFeedbackLogService.getLogs(memberId, startDate, endDate);
    }
}
