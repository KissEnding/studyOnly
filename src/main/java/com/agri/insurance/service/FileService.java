package com.agri.insurance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.nio.file.StandardCopyOption;
import java.io.File;
import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created upload directory: {}", path);
            }
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", uploadPath, e);
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String uploadFile(String type, MultipartFile file) throws IOException {
        log.info("Processing file upload for type: {}", type);
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持上传图片文件");
        }

        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过5MB");
        }

        // 创建上传目录
        String directory = uploadPath + File.separator + type;
        Path directoryPath = Paths.get(directory).toAbsolutePath();
        log.info("Upload directory: {}", directoryPath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = directoryPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        if (Files.exists(filePath)) {
            log.info("File saved successfully at: {}", filePath);
        } else {
            log.error("File was not saved properly");
            throw new IOException("Failed to save file");
        }
        
        // 返回文件URL（注意：这里不需要包含 type 目录）
        String fileUrl = "/api/uploads/" + type + "/" + filename;
        log.info("File URL: {}", fileUrl);
        return fileUrl;
    }
} 