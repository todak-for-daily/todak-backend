package com.example.todak_server.controller;

import com.example.todak_server.dto.request.AccessPermissionUpdateRequest;
import com.example.todak_server.dto.request.EmployeeRegisterRequest;
import com.example.todak_server.dto.request.OrganizationUnitCreateRequest;
import com.example.todak_server.dto.request.OrganizationUnitUpdateRequest;
import com.example.todak_server.dto.response.EmployeeDetailResponse;
import com.example.todak_server.dto.response.EmployeeResponse;
import com.example.todak_server.dto.response.OrganizationUnitResponse;
import com.example.todak_server.service.OrganizationUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "조직 & 직원 관리 API", description = "조직 트리, 직원 등록/조회/권한설정 등 관리자 기능 API")
@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrganizationUnitController {

    private final OrganizationUnitService organizationUnitService;

    // 조직 조회
    @Operation(summary = "조직 트리 조회", description = "최상위 조직부터 전체 조직 구조(트리)를 조회함.")
    @GetMapping("/tree")
    public ResponseEntity<List<OrganizationUnitResponse>> getTree() {
        return ResponseEntity.ok(organizationUnitService.getOrganizationTree());
    }

    // 조직 등록
    @Operation(summary = "조직 등록", description = "새로운 조직을 생성하고, 부모 조직 ID를 전달하면 하위 조직으로 등록함.")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrganizationUnitCreateRequest request) {
        Long id = organizationUnitService.createOrganizationUnit(request);
        return ResponseEntity.ok(id);
    }

    // 조직 수정
    @Operation(summary = "조직 수정", description = "조직의 이름 또는 부모 조직을 변경함.")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody OrganizationUnitUpdateRequest request
    ) {
        organizationUnitService.updateOrganizationUnit(id, request);
        return ResponseEntity.ok().build();
    }

    // 직원 등록
    @Operation(summary = "직원 등록", description = "이메일/닉네임/조직ID를 입력하여 직원을 생성하거나 기존 멤버를 조직에 배치함.")
    @PostMapping("/employees/register")
    public ResponseEntity<Long> registerEmployeeByEmail(
            @RequestBody EmployeeRegisterRequest request
    ) {
        Long memberId = organizationUnitService.registerEmployeeByName(request);
        return ResponseEntity.ok(memberId);
    }

    // 직원 조회
    @Operation(summary = "직원 목록 조회", description = "전체 직원 또는 특정 조직에 속한 직원 목록을 조회함.")
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
            @RequestParam(required = false) Long orgId
    ) {
        return ResponseEntity.ok(organizationUnitService.getEmployees(orgId));
    }

    // 직원 상세 조회
    @Operation(summary = "직원 상세 조회", description = "회원ID를 기준으로 직원의 상세 정보와 권한을 조회함.")
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDetailResponse> getEmployeeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(organizationUnitService.getEmployeeDetail(id));
    }

    // 직원 권한 설정
    @Operation(summary = "직원 권한 수정", description = "직원의 권한(스케줄 조회, 경고 조회, 건강 조회)을 수정함.")
    @PatchMapping("/employees/{id}/permissions")
    public ResponseEntity<Void> updatePermission(
            @PathVariable Long id,
            @RequestBody AccessPermissionUpdateRequest request
    ) {
        organizationUnitService.updateEmployeePermission(id, request);
        return ResponseEntity.ok().build();
    }

    // 조직 삭제
    @Operation(summary = "조직 삭제", description = "조직을 삭제함.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationUnitService.deleteOrganization(id);
        return ResponseEntity.ok().build();
    }

    // 직원 삭제
    @Operation(summary = "직원 삭제", description = "직원을 특정 조직 내에서 삭제함.")
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        organizationUnitService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

}