package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;      // 이메일

    @Column(nullable = false)
    private String name;       // 이름

    private String phone;      // 전화번호
    private String role;       // 직책
    private String avatarUrl;  // 프로필 사진 URL

    public void updateProfile(String name, String phone, String avatarUrl, String role) {
        this.name = name;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }
}
