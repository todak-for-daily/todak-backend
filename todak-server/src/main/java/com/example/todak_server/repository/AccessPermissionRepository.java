package com.example.todak_server.repository;

import com.example.todak_server.entity.AccessPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessPermissionRepository extends JpaRepository<AccessPermission, Long> {}
