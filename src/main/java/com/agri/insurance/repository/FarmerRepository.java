package com.agri.insurance.repository;

import com.agri.insurance.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    @Query("SELECT f FROM Farmer f WHERE LOWER(f.username) = LOWER(:username)")
    Optional<Farmer> findByUsername(@Param("username") String username);
    
    List<Farmer> findByStatus(String status);
    
    // 不需要重写 findAll，JpaRepository 已经提供了这个方法
} 