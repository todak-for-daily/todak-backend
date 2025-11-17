package com.example.todak_server.service;

import com.example.todak_server.dto.request.AccessPermissionUpdateRequest;
import com.example.todak_server.dto.request.EmployeeRegisterRequest;
import com.example.todak_server.dto.request.OrganizationUnitCreateRequest;
import com.example.todak_server.dto.request.OrganizationUnitUpdateRequest;
import com.example.todak_server.dto.response.EmployeeDetailResponse;
import com.example.todak_server.dto.response.EmployeeResponse;
import com.example.todak_server.dto.response.OrganizationUnitResponse;
import com.example.todak_server.entity.AccessPermission;
import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.OrganizationUnit;
import com.example.todak_server.entity.Role;
import com.example.todak_server.repository.AccessPermissionRepository;
import com.example.todak_server.repository.MemberRepository;
import com.example.todak_server.repository.OrganizationUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationUnitService {

    private final OrganizationUnitRepository organizationUnitRepository;
    private final MemberRepository memberRepository;
    private final AccessPermissionRepository accessPermissionRepository;


    // 조직 트리 전체 조회
    public List<OrganizationUnitResponse> getOrganizationTree() {
        List<OrganizationUnit> roots = organizationUnitRepository.findByParentIsNull();

        return roots.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 재귀 - Entity → Response 변환
    private OrganizationUnitResponse convertToResponse(OrganizationUnit unit) {
        List<OrganizationUnitResponse> childResponses = unit.getChildren().stream()
                .map(this::convertToResponse)
                .toList();

        return new OrganizationUnitResponse(
                unit.getId(),
                unit.getName(),
                childResponses
        );
    }

    // 조직 생성
    @Transactional
    public Long createOrganizationUnit(OrganizationUnitCreateRequest request) {

        OrganizationUnit parent = null;

        if (request.parentId() != null) {
            parent = organizationUnitRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        }

        OrganizationUnit unit = new OrganizationUnit();
        unit.setName(request.name());
        unit.setParent(parent);

        OrganizationUnit saved = organizationUnitRepository.save(unit);
        return saved.getId();
    }

    // 조직 수정
    @Transactional
    public void updateOrganizationUnit(Long id, OrganizationUnitUpdateRequest request) {

        OrganizationUnit unit = organizationUnitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        if (request.name() != null) {
            unit.setName(request.name());
        }

        if (request.parentId() != null) {
            OrganizationUnit parent = organizationUnitRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));

            unit.setParent(parent);
        }
    }

    // 직원 등록
    @Transactional
    public Long registerEmployeeByName(EmployeeRegisterRequest request) {

        // 1) 이메일로 멤버 찾기
        Member employee = memberRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    // 없다면 새로 생성
                    Member newEmp = new Member();
                    newEmp.setNickname(request.nickname());
                    newEmp.setEmail(request.email());
                    newEmp.setRole(Role.USER);
                    return memberRepository.save(newEmp);
                });

        // 2) 조직 이름으로 조직 찾기
        OrganizationUnit org = organizationUnitRepository
                .findById(request.organizationUnitId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + request.organizationUnitId()));

        // 3) 멤버의 조직 업데이트
        employee.setOrganizationUnit(org);

        // 저장 후 멤버 id 반환
        memberRepository.save(employee);

        return employee.getId();
    }

    // 전체 직원 조회
    public List<EmployeeResponse> getEmployees(Long organizationId) {

        List<Member> members;

        if (organizationId == null) {
            members = memberRepository.findAll();
        } else {
            members = memberRepository.
                    findByOrganizationUnitId(organizationId);
        }

        return members.stream()
                .map(m -> new EmployeeResponse(
                        m.getId(),
                        m.getEmail(),
                        m.getNickname(),
                        m.getRole().name(),
                        m.getOrganizationUnit() != null ? m.getOrganizationUnit().getName() : null
                ))
                .toList();
    }

    // 직원 상세 조회
    public EmployeeDetailResponse getEmployeeDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        return new EmployeeDetailResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getRole().name(),
                member.getOrganizationUnit() != null ? member.getOrganizationUnit().getName() : null,
                member.getAccessPermission() != null && member.getAccessPermission().isCanViewSchedule(),
                member.getAccessPermission() != null && member.getAccessPermission().isCanViewWarning(),
                member.getAccessPermission() != null && member.getAccessPermission().isCanViewHealth()
        );
    }

    // 직원 권한 설정
    @Transactional
    public void updateEmployeePermission(Long memberId, AccessPermissionUpdateRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        AccessPermission permission = member.getAccessPermission();

        // 직원 권한이 아직 없으면 새로 생성
        if (permission == null) {
            permission = new AccessPermission();
            permission.setMember(member);
        }

        permission.setCanViewSchedule(request.canViewSchedule());
        permission.setCanViewWarning(request.canViewWarning());
        permission.setCanViewHealth(request.canViewHealth());

        // 저장
        accessPermissionRepository.save(permission);
    }

    // 조직 삭제
    @Transactional
    public void deleteOrganization(Long id) {
        OrganizationUnit unit = organizationUnitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        if (!unit.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete organization that has sub-organizations");
        }

        // 직원도 남아있으면 삭제 불가
        if (memberRepository.existsByOrganizationUnit(unit)) {
            throw new IllegalStateException("Cannot delete organization that has employees");
        }

        organizationUnitRepository.delete(unit);
    }

    // 직원 삭제
    @Transactional
    public void deleteEmployee(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        memberRepository.delete(member);
    }

}


