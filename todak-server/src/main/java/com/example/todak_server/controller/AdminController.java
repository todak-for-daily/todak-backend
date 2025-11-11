package com.example.todak_server.controller;

import com.example.todak_server.dto.request.AdminProfileRequest;
import com.example.todak_server.dto.response.AdminProfileResponse;
import com.example.todak_server.entity.Admin;
import com.example.todak_server.repository.AdminRepository;
import com.example.todak_server.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // JWT 인증 붙이기 전 임시로 1L 사용
    private static final Long ADMIN_ID = 1L;

    @GetMapping
    public AdminProfileResponse getProfile() {
        var admin = adminService.getAdminById(ADMIN_ID);
        return AdminProfileResponse.from(admin);
    }

    @PostMapping
    public ResponseEntity<AdminProfileResponse> createAdmin(@RequestBody AdminProfileRequest request) {
        Admin created = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminProfileResponse.from(created));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<AdminProfileResponse> updateProfilePartially(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Admin updated = adminService.updatePartialAdmin(id, updates);
        return ResponseEntity.ok(AdminProfileResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdminById(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/avatar-url")   // 프로필 사진
//    public Map<String, String> issueAvartarUploadUrl() {
//    }
}
