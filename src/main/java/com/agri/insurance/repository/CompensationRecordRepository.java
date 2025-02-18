package com.agri.insurance.repository;

import com.agri.insurance.model.CompensationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompensationRecordRepository extends JpaRepository<CompensationRecord, Long> {
    // 根据保单ID查询理赔记录
    @Query("SELECT c FROM CompensationRecord c JOIN c.policy p WHERE p.id = :policyId")
    Page<CompensationRecord> findByPolicyId(@Param("policyId") Long policyId, Pageable pageable);
    
    // 根据保单ID和状态查询理赔记录
    @Query("SELECT c FROM CompensationRecord c JOIN c.policy p WHERE p.id = :policyId AND c.status = :status")
    Page<CompensationRecord> findByPolicyIdAndStatus(@Param("policyId") Long policyId, @Param("status") Boolean status, Pageable pageable);
    
    // 根据农户用户名查询理赔记录
    @Query("SELECT c FROM CompensationRecord c JOIN c.policy p JOIN p.farmer f WHERE f.username = :username")
    Page<CompensationRecord> findByPolicy_Farmer_Username(@Param("username") String username, Pageable pageable);
    
    // 根据农户用户名和状态查询理赔记录
    @Query("SELECT c FROM CompensationRecord c JOIN c.policy p JOIN p.farmer f WHERE f.username = :username AND c.status = :status")
    Page<CompensationRecord> findByPolicy_Farmer_UsernameAndStatus(@Param("username") String username, @Param("status") Boolean status, Pageable pageable);
} 