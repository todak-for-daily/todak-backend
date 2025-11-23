package com.example.todak_server.controller;

import com.example.todak_server.dto.request.CompanyCreateRequest;
import com.example.todak_server.dto.request.CompanyUpdateRequest;
import com.example.todak_server.dto.response.CompanyResponse;
import com.example.todak_server.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회사 관리 API", description = "회사 생성/수정/조회/삭제 등 회사 관련 관리자 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "회사 등록", description = "회사 이름, 대표자 정보 등 기본 정보를 입력하여 새로운 회사를 생성합니다.")
    @PostMapping
    public CompanyResponse create(@RequestBody CompanyCreateRequest req) {
        return companyService.create(req);
    }

    @Operation(summary = "회사 수정", description = "회사 ID로 회사를 조회한 뒤, 회사명 또는 기본 정보를 수정합니다.")
    @PutMapping("/{companyId}")
    public CompanyResponse update(
            @PathVariable("companyId") Long companyId,
            @RequestBody CompanyUpdateRequest req
    ) {
        return companyService.update(companyId, req);
    }

    @Operation(summary = "회사 조회", description = "companyId를 기준으로 하나의 회사 정보를 조회합니다.")
    @GetMapping("/{companyId}")
    public CompanyResponse get(@PathVariable("companyId") Long companyId) {
        return companyService.get(companyId);
    }

    @Operation(summary = "전체 회사 목록 조회", description = "등록된 모든 회사 목록을 조회합니다.")
    @GetMapping
    public List<CompanyResponse> getAll() {
        return companyService.getAll();
    }

    @Operation(summary = "회사 삭제", description = "특정 회사 ID로 회사를 삭제합니다.")
    @DeleteMapping("/{companyId}")
    public void delete(@PathVariable("companyId") Long companyId) {
        companyService.delete(companyId);
    }
}
