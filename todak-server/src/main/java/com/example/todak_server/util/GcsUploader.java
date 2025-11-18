package com.example.todak_server.util;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GcsUploader {

    @Value("${gcp.storage.bucket}")
    private String bucket;

    private final Storage storage;


    // 이미지 업로드
    public String upload(MultipartFile file, String dirName) {
        try {
            String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucket, fileName)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            return String.format("https://storage.googleapis.com/%s/%s", bucket, fileName);

        } catch (IOException e) {
            throw new RuntimeException("GCS 파일 업로드 실패: " + e.getMessage());
        }
    }

    // 이미지 삭제
    public void delete(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.indexOf("/", 50) + 1);
            storage.delete(bucket, fileName);
        } catch (Exception e) {
            throw new RuntimeException("GCS 파일 삭제 실패");
        }
    }
}
