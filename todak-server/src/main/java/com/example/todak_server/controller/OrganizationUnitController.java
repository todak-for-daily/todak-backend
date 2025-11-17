package com.example.todak_server.controller;

import com.example.todak_server.dto.request.AccessPermissionUpdateRequest;
import com.example.todak_server.dto.request.EmployeeRegisterRequest;
import com.example.todak_server.dto.request.OrganizationUnitCreateRequest;
import com.example.todak_server.dto.request.OrganizationUnitUpdateRequest;
import com.example.todak_server.dto.response.EmployeeDetailResponse;
import com.example.todak_server.dto.response.EmployeeResponse;
import com.example.todak_server.dto.response.OrganizationUnitResponse;
import com.example.todak_server.service.OrganizationUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrganizationUnitController {

    private final OrganizationUnitService organizationUnitService;

    // 조직 조회
    @GetMapping("/tree")
    public ResponseEntity<List<OrganizationUnitResponse>> getTree() {
        return ResponseEntity.ok(organizationUnitService.getOrganizationTree());
    }

    // 조직 등록
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrganizationUnitCreateRequest request) {
        Long id = organizationUnitService.createOrganizationUnit(request);
        return ResponseEntity.ok(id);
    }

    // 조직 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody OrganizationUnitUpdateRequest request
    ) {
        organizationUnitService.updateOrganizationUnit(id, request);
        return ResponseEntity.ok().build();
    }

    // 직원 등록
    @PostMapping("/employees/register")
    public ResponseEntity<Long> registerEmployeeByEmail(
            @RequestBody EmployeeRegisterRequest request
    ) {
        Long memberId = organizationUnitService.registerEmployeeByName(request);
        return ResponseEntity.ok(memberId);
    }

    // 직원 조회
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
            @RequestParam(required = false) Long orgId
    ) {
        return ResponseEntity.ok(organizationUnitService.getEmployees(orgId));
    }

    // 직원 상세 조회
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDetailResponse> getEmployeeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(organizationUnitService.getEmployeeDetail(id));
    }

    // 직원 권한 설정
    @PatchMapping("/employees/{id}/permissions")
    public ResponseEntity<Void> updatePermission(
            @PathVariable Long id,
            @RequestBody AccessPermissionUpdateRequest request
    ) {
        organizationUnitService.updateEmployeePermission(id, request);
        return ResponseEntity.ok().build();
    }

    // 조직 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationUnitService.deleteOrganization(id);
        return ResponseEntity.ok().build();
    }

    // 직원 삭제
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        organizationUnitService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

}