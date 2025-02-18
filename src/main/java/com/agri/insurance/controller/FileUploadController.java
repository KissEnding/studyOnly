package com.agri.insurance.controller;

import com.agri.insurance.dto.ErrorResponse;
import com.agri.insurance.dto.FileUploadResponse;
import com.agri.insurance.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@CrossOrigin
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    @PostMapping("/{type}")
    public ResponseEntity<?> uploadFile(@PathVariable String type, @RequestParam("file") MultipartFile file) {
        try {
            log.info("Uploading file for type: {}, original filename: {}", type, file.getOriginalFilename());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("文件不能为空"));
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(new ErrorResponse("只支持上传图片文件"));
            }
            
            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(new ErrorResponse("文件大小不能超过5MB"));
            }
            
            String fileUrl = fileService.uploadFile(type, file);
            log.info("File uploaded successfully. URL: {}", fileUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("File upload failed: ", e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
} 