package com.example.todak_server.service;

import com.example.todak_server.dto.request.AdminProfileRequest;
import com.example.todak_server.entity.Admin;
import com.example.todak_server.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
    }

    @Transactional
    public Admin createAdmin(AdminProfileRequest request) {
        Admin admin = new Admin();
        admin.setEmail(request.email());
        admin.setName(request.name());
        admin.setPhone(request.phone());
        admin.setAvatarUrl(request.avatarUrl());
        admin.setRole(request.role());

        return adminRepository.save(admin);
    }


    @Transactional
    public Admin updatePartialAdmin(Long id, Map<String, Object> updates) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (updates.containsKey("name")) admin.setName((String) updates.get("name"));
        if (updates.containsKey("email")) admin.setEmail((String) updates.get("email"));
        if (updates.containsKey("phone")) admin.setPhone((String) updates.get("phone"));
        if (updates.containsKey("avatarUrl")) admin.setAvatarUrl((String) updates.get("avatarUrl"));

        return adminRepository.save(admin);
    }

    @Transactional
    public void deleteAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        adminRepository.delete(admin);
    }


}
