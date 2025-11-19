package com.example.todak_server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 프로필 수정 요청 DTO")
public record AdminProfileRequest(

        @Schema(description = "이메일", example = "admin@test.com")
        String email,

        @Schema(description = "관리자 이름", example = "김관리")
        String name,

        @Schema(description = "전화번호", example = "010-2222-3333")
        String phone,

        @Schema(description = "프로필 이미지 URL", example = "https://storage.googleapis.com/.../avatar1.png")
        String avatarUrl,

        @Schema(description = "역할", example = "ADMIN")
        String role
) {}
