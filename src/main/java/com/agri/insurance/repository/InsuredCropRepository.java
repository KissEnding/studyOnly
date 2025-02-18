package com.agri.insurance.repository;

import com.agri.insurance.model.InsuredCrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuredCropRepository extends JpaRepository<InsuredCrop, Long> {
    List<InsuredCrop> findByCropCategory(String cropCategory);
} 