package com.example.demojpa.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demojpa.dto.ApiResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/upload")
@PreAuthorize("hasRole('ADMIN')")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads";
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${server.port}")
    private String serverPort;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(400, "Vui lòng chọn file để upload!", null));
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
            String fileUrl = String.format("%s:%s/uploads/%s", baseUrl, serverPort, file.getOriginalFilename());

            return ResponseEntity.ok(new ApiResponse<>(200, "Upload thành công", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "Lỗi upload file!", null));
        }
    }
}
