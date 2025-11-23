package com.example.todak_server.service;

import com.example.todak_server.dto.request.CompanyCreateRequest;
import com.example.todak_server.dto.request.CompanyUpdateRequest;
import com.example.todak_server.dto.response.CompanyResponse;
import com.example.todak_server.entity.Company;
import com.example.todak_server.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponse create(CompanyCreateRequest req) {
        Company c = Company.builder()
                .name(req.name())
                .description(req.description())
                .build();

        companyRepository.save(c);

        return toResponse(c);
    }

    public CompanyResponse update(Long companyId, CompanyUpdateRequest req) {
        Company c = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("기업을 찾을 수 없습니다."));

        c.setName(req.name());
        c.setDescription(req.description());

        companyRepository.save(c);

        return toResponse(c);
    }

    public void delete(Long companyId) {
        companyRepository.deleteById(companyId);
    }

    public CompanyResponse get(Long companyId) {
        Company c = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("기업을 찾을 수 없습니다."));

        return toResponse(c);
    }

    public List<CompanyResponse> getAll() {
        return companyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CompanyResponse toResponse(Company c) {
        return new CompanyResponse(
                c.getId(),
                c.getName(),
                c.getDescription()
        );
    }
}
