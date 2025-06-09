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

    private final String uploadDir = "/Users/ijuhyeon/juhyeon/computer/SSAFY/JAVA/springstudy/spring-daangn/uploads"; // 절대 경로로 수정
    private final String baseUrl = "/uploads"; // 프론트에서 접근 가능한 URL 경로

    @Override
    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalName = file.getOriginalFilename();
        String savedName = UUID.randomUUID() + "_" + originalName;
        Path savePath = Paths.get(uploadDir, savedName);

        try {
            Files.createDirectories(savePath.getParent()); // 디렉토리 없으면 생성
            file.transferTo(savePath); // 실제 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e); // 예외 출력 개선해도 좋음
        }

        return baseUrl + "/" + savedName; // 접근 가능한 URL로 반환
    }

    @Override
    public void delete(String fileUrl) {
        String filename = Paths.get(fileUrl).getFileName().toString(); // ✅ URI 사용 안함
        Path filePath = Paths.get(uploadDir, filename);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + filename, e);
        }
    }
}
