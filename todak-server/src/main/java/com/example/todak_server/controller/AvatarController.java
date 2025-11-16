package com.example.todak_server.controller;

import com.example.todak_server.file.FirebaseStorageUploader;
import com.example.todak_server.service.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "사용자 본인 이미지", description = "사용자 본인의 이미지 등록 및 가져오기")
@RestController
@RequiredArgsConstructor
public class AvatarController {

    private final FirebaseStorageUploader uploader;
    private final AvatarService memberService;

    @Operation(summary = "사용자 이미지 등록")
    @PostMapping(value = "/api/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestPart("file")MultipartFile file
            ) {
        String imageUrl = uploader.uploadAvatar(memberId, file);
        memberService.updateAvatarUrl(memberId, imageUrl);
        return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
    }

    @Operation(summary = "사용자 이미지 불러오기")
    @GetMapping("/api/me/avatar")
    public ResponseEntity<Map<String, String>> getAvatar(
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        String url = memberService.getAvatarUrl(memberId);
        return ResponseEntity.ok().body(Map.of("imageUrl", url));
    }
}
