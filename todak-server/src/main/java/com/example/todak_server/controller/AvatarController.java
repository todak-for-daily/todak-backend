package com.example.todak_server.controller;

import com.example.todak_server.file.FirebaseStorageUploader;
import com.example.todak_server.service.AvatarService;
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

@RestController
@RequiredArgsConstructor
public class AvatarController {

    private final FirebaseStorageUploader uploader;
    private final AvatarService memberService;

    @PostMapping(value = "/api/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestPart("file")MultipartFile file
            ) {
        String imageUrl = uploader.uploadAvatar(memberId, file);
        memberService.updateAvatarUrl(memberId, imageUrl);
        return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
    }

    @GetMapping("/api/me/avatar")
    public ResponseEntity<Map<String, String>> getAvatar(
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        String url = memberService.getAvatarUrl(memberId);
        return ResponseEntity.ok().body(Map.of("imageUrl", url));
    }
}
