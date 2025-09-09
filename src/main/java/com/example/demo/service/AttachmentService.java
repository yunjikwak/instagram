package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    public String uploadFile(MultipartFile file) {
        String filePath = "images/post/temp-" + file.getOriginalFilename(); // 임시 경로 생성
        return filePath;
    }
}
