package com.agri.insurance.controller;

import com.agri.insurance.model.CompensationRecord;
import com.agri.insurance.service.CompensationRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/farmer/claims")
@CrossOrigin
@RequiredArgsConstructor
public class FarmerClaimController {

    private final CompensationRecordService compensationRecordService;

    /**
     * 获取当前用户的理赔记录
     */
    @GetMapping
    public ResponseEntity<?> getClaimRecords(
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting claim records for farmer: {}", username);
        
        try {
            Page<CompensationRecord> recordPage = compensationRecordService.getUserCompensationRecords(
                    username, status, PageRequest.of(page, size));
                    
            Map<String, Object> response = new HashMap<>();
            response.put("content", recordPage.getContent());
            response.put("total", recordPage.getTotalElements());
            response.put("page", recordPage.getNumber());
            response.put("size", recordPage.getSize());
            response.put("totalPages", recordPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting claim records", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取理赔记录失败");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取指定理赔记录的详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getClaimRecord(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting claim record details, id: {}, farmer: {}", id, username);
        
        try {
            CompensationRecord record = compensationRecordService.getCompensationRecord(id);
            
            // 验证记录是否属于当前用户
            if (!record.getPolicy().getFarmer().getUsername().equals(username)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "无权访问此理赔记录");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            log.error("Error getting claim record details", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "获取理赔记录详情失败");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 