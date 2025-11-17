package com.example.todak_server.controller;

import com.example.todak_server.dto.request.AdminProfileRequest;
import com.example.todak_server.dto.response.AdminProfileResponse;
import com.example.todak_server.entity.Admin;
import com.example.todak_server.repository.AdminRepository;
import com.example.todak_server.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "관리자 프로필 API", description = "관리자의 프로필 조회/수정/삭제 기능 API")
@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // JWT 인증 붙이기 전 임시로 1L 사용
    private static final Long ADMIN_ID = 1L;

    @Operation(summary = "관리자 프로필 조회", description = "현재 관리자 계정의 프로필 정보를 조회함.")
    @GetMapping
    public AdminProfileResponse getProfile() {
        var admin = adminService.getAdminById(ADMIN_ID);
        return AdminProfileResponse.from(admin);
    }

    @Operation(summary = "관리자 생성", description = "새로운 관리자 계정을 생성함.")
    @PostMapping
    public ResponseEntity<AdminProfileResponse> createAdmin(@RequestBody AdminProfileRequest request) {
        Admin created = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminProfileResponse.from(created));
    }

    @Operation(summary = "관리자 정보 수정(부분 수정)", description = "관리자의 특정 필드만 선택적으로 수정함.")
    @PatchMapping("/{id}")
    public ResponseEntity<AdminProfileResponse> updateProfilePartially(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Admin updated = adminService.updatePartialAdmin(id, updates);
        return ResponseEntity.ok(AdminProfileResponse.from(updated));
    }

    @Operation(summary = "관리자 삭제", description = "관리자 계정을 삭제함.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdminById(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/avatar-url")   // 프로필 사진
//    public Map<String, String> issueAvartarUploadUrl() {
//    }
}
