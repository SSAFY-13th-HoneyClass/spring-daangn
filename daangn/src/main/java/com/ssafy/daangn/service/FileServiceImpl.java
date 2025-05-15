package com.ssafy.daangn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final String uploadDir = "/uploads"; // 실제 저장 경로
    private final String baseUrl = "/Users/ijuhyeon/juhyeon/computer/SSAFY/JAVA/springstudy/spring-daangn"; // 접근 경로 (프론트가 접근 가능해야 함)

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalName = file.getOriginalFilename();
        String savedName = UUID.randomUUID() + "_" + originalName;
        Path savePath = Paths.get(uploadDir, savedName);

        try {
            file.transferTo(savePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // URL 생성해서 반환
        return baseUrl + "/" + savedName;
    }

    @Override
    public void delete(String fileUrl) {
        // 1. 파일명 추출
        String filename = Paths.get(URI.create(fileUrl).getPath()).getFileName().toString();

        // 2. 파일 경로
        Path filePath = Paths.get(uploadDir, filename);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + filename, e);
        }
    }
}
