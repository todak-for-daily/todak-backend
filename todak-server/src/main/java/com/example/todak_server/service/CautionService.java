package com.example.todak_server.service;

import com.example.todak_server.dto.request.CautionCreateRequest;
import com.example.todak_server.dto.response.CautionResponse;
import com.example.todak_server.entity.*;
import com.example.todak_server.repository.*;
import com.example.todak_server.util.GcsUploader;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CautionService {

    private final CautionRepository cautionRepo;
    private final CautionReadRepository cautionReadRepo;
    private final AdminRepository adminRepo;
    private final MemberRepository memberRepo;
    private final GcsUploader gcsUploader;

    // 1) 관리자: 주의사항 등록
    @Transactional
    public CautionResponse create(CautionCreateRequest req, MultipartFile file) {

        Admin manager = adminRepo.findById(req.managerId())
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = gcsUploader.upload(file, "cautions");
        }

        Caution caution = cautionRepo.save(
                Caution.builder()
                        .title(req.title())
                        .description(req.description())
                        .notifyAfterHours(req.notifyAfterHours())
                        .manager(manager)
                        .fileUrl(fileUrl)
                        .build()
        );

        // 등록된 조직의 모든 직원에게 CautionRead 생성
        List<Member> members = memberRepo.findAll();
        for (Member m : members) {
            cautionReadRepo.save(
                    CautionRead.builder()
                            .memberId(m.getId())
                            .caution(caution)
                            .build()
            );
        }

        return CautionResponse.builder()
                .id(caution.getId())
                .title(caution.getTitle())
                .description(caution.getDescription())
                .fileUrl(caution.getFileUrl())
                .notifyAfterHours(caution.getNotifyAfterHours())
                .isRead(false)
                .createdAt(caution.getCreatedAt())
                .build();
    }

    // 2) 직원: 주의사항 목록 조회
    @Transactional(readOnly = true)
    public List<CautionResponse> getList(Long memberId) {

        List<CautionRead> reads = cautionReadRepo.findByMemberId(memberId);

        return reads.stream()
                .map(cr -> {
                    Caution c = cr.getCaution();
                    return CautionResponse.builder()
                            .id(c.getId())
                            .title(c.getTitle())
                            .description(c.getDescription())
                            .fileUrl(c.getFileUrl())
                            .isRead(cr.isRead())
                            .notifyAfterHours(c.getNotifyAfterHours())
                            .createdAt(c.getCreatedAt())
                            .build();
                }).toList();
    }

    // 3) 직원: 읽음 처리
    @Transactional
    public void markAsRead(Long memberId, Long cautionId) {

        CautionRead cr = cautionReadRepo.findByMemberIdAndCautionId(memberId, cautionId)
                .orElseThrow(() -> new RuntimeException("주의사항 없음"));

        cr = CautionRead.builder()
                .id(cr.getId())
                .memberId(memberId)
                .caution(cr.getCaution())
                .isRead(true)
                .notified(cr.isNotified())
                .readAt(LocalDateTime.now())
                .build();

        cautionReadRepo.save(cr);
    }
}
