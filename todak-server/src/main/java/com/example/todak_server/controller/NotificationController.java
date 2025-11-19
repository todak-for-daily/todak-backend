package com.example.todak_server.controller;

import com.example.todak_server.dto.request.NotificationRequest;
import com.example.todak_server.dto.response.NotificationResponse;
import com.example.todak_server.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공지 및 알림 API", description = "공지 발송, 알림 조회, 읽음 처리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "공지/알림 전송",
            description = """
                    관리자가 공지 또는 알림을 발송함.
                    
                    • targetMemberIds가 비어있으면 전체 발송  
                    • 전송 즉시 FCM 푸시가 나가며  
                    • DB(Notification + NotificationRead)에 기록됨.
                    """
    )
    @PostMapping("/send")
    public String sendNotice(@RequestBody NotificationRequest req) {
        notificationService.sendNotice(req.adminId(),
                req.title(),
                req.body(),
                req.targetMemberIds());
        return "OK";
    }

    @Operation(
            summary = "알림 목록 조회",
            description = """
                    특정 직원(memberId)의 전체 알림 목록을 조회함.  
                    읽음 여부, 생성 시간 등이 포함됨.
                    """
    )
    @GetMapping("/member/{memberId}")
    public List<NotificationResponse> getNotifications(@PathVariable Long memberId) {
        return notificationService.getMemberNotifications(memberId);
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = """
                    특정 직원이 특정 알림(notificationId)을 읽었음을 기록함.  
                    NotificationRead 테이블의 isRead가 true로 업데이트됨.
                    """
    )
    @PostMapping("/read")
    public String markAsRead(@RequestParam Long memberId,
                             @RequestParam Long notificationId) {
        notificationService.markAsRead(notificationId, memberId);
        return "OK";
    }
}
