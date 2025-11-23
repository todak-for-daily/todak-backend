package com.example.todak_server.repository;

import com.example.todak_server.entity.OrganizationUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationUnitRepository extends JpaRepository<OrganizationUnit, Long> {

    // 부모가 없는 최상위 조직만 조회
    List<OrganizationUnit> findByParentIsNull();
    Optional<OrganizationUnit> findByName(String name);
    List<OrganizationUnit> findByCompanyIdAndParentIsNull(Long companyId);

}
