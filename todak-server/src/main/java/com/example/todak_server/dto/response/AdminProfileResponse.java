package com.example.todak_server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.todak_server.entity.Admin;

@Schema(description = "관리자 프로필 응답 DTO")
public record AdminProfileResponse(

        @Schema(description = "관리자 ID", example = "1")
        Long id,

        @Schema(description = "이메일", example = "admin@test.com")
        String email,

        @Schema(description = "이름", example = "관리자 김철수")
        String name,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phone,

        @Schema(description = "아바타 이미지 URL", example = "https://storage.googleapis.com/.../avatar01.jpg")
        String avartarUrl,

        @Schema(description = "역할", example = "ADMIN")
        String role
) {

    public static AdminProfileResponse from(Admin admin) {
        return new AdminProfileResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getName(),
                admin.getPhone(),
                admin.getAvatarUrl(),
                admin.getRole()
        );
    }
}
