package com.agri.insurance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class ApplicationStartupListener {

    @Value("${file.upload.path}")
    private String uploadPath;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            Path uploadsPath = Paths.get(uploadPath).toAbsolutePath();
            log.info("Initializing upload directory at: {}", uploadsPath);

            // 创建主目录
            if (!Files.exists(uploadsPath)) {
                Files.createDirectories(uploadsPath);
                log.info("Created main upload directory");
            }

            // 创建子目录
            Path farmerPath = uploadsPath.resolve("farmer");
            Path companyPath = uploadsPath.resolve("company");

            if (!Files.exists(farmerPath)) {
                Files.createDirectories(farmerPath);
                log.info("Created farmer upload directory");
            }

            if (!Files.exists(companyPath)) {
                Files.createDirectories(companyPath);
                log.info("Created company upload directory");
            }

            log.info("File upload directories initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize upload directories", e);
            throw new RuntimeException("Failed to initialize upload directories", e);
        }
    }
} 