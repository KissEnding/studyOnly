package com.agri.insurance.service;

import com.agri.insurance.model.CompensationRecord;
import com.agri.insurance.repository.CompensationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompensationRecordService {

    private final CompensationRecordRepository compensationRecordRepository;

    /**
     * 获取用户的理赔记录
     */
    public Page<CompensationRecord> getUserCompensationRecords(String username, Boolean status, Pageable pageable) {
        log.info("Getting compensation records for username: {}, status: {}", username, status);
        if (status != null) {
            return compensationRecordRepository.findByPolicy_Farmer_UsernameAndStatus(username, status, pageable);
        }
        return compensationRecordRepository.findByPolicy_Farmer_Username(username, pageable);
    }

    /**
     * 获取保单的理赔记录
     */
    public Page<CompensationRecord> getPolicyCompensationRecords(Long policyId, Boolean status, Pageable pageable) {
        if (status != null) {
            return compensationRecordRepository.findByPolicyIdAndStatus(policyId, status, pageable);
        }
        return compensationRecordRepository.findByPolicyId(policyId, pageable);
    }

    /**
     * 创建理赔记录
     */
    @Transactional
    public CompensationRecord createCompensationRecord(CompensationRecord record) {
        return compensationRecordRepository.save(record);
    }

    /**
     * 更新理赔记录
     */
    @Transactional
    public CompensationRecord updateCompensationRecord(CompensationRecord record) {
        return compensationRecordRepository.save(record);
    }

    /**
     * 根据ID获取理赔记录
     */
    public CompensationRecord getCompensationRecord(Long id) {
        log.info("Getting compensation record by id: {}", id);
        return compensationRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("理赔记录不存在"));
    }
} 