package com.example.todak_server.file;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class FirebaseStorageUploader {
    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","image/webp");

    public String uploadAvatar(Long memberId, MultipartFile file) {
        try {
            if (file.isEmpty() || !ALLOWED.contains(file.getContentType()))
                throw new IllegalArgumentException("이미지(jpg/png/webp)만 허용됩니다.");
            if (file.getSize() > 5 * 1024 * 1024)
                throw new IllegalArgumentException("최대 5MB까지 허용됩니다.");

            String ext = switch(String.valueOf(file.getContentType())) {
                case "image/jpeg" -> "jpg";
                case "image/png" -> "png";
                case "image/webp" -> "webp";
                default -> "bin";
            };
            String objectPath = "avatars/%d/%d.%s".formatted(memberId, System.currentTimeMillis(), ext);

            Bucket bucket = StorageClient.getInstance().bucket();
            Storage storage = bucket.getStorage();
            String bucketName = bucket.getName();

            String downloadToken = UUID.randomUUID().toString();
            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectPath))
                    .setContentType(file.getContentType())
                    .setMetadata(Map.of("firebaseStorageDownloadTokens",downloadToken))
                    .build();

            storage.create(blobInfo, file.getBytes());

            //  공개 다운로드 URL 생성
            String encodedPath = URLEncoder.encode(objectPath, StandardCharsets.UTF_8);
            return "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s"
                    .formatted(bucketName, encodedPath, downloadToken);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }
}
