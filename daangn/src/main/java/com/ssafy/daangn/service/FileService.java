package com.ssafy.daangn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    public String upload(MultipartFile file);
    public void delete(String fileUrl);
}
