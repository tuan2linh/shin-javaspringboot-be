package com.example.demojpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Vui lòng chọn file để upload!", HttpStatus.BAD_REQUEST);
        }

        try {
            // Tạo thư mục nếu chưa có
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lưu file
            String filePath = UPLOAD_DIR + "/" + file.getOriginalFilename();
            Path path = Paths.get(filePath);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("Upload thành công: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi upload file!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
