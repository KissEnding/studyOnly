package com.agri.insurance.repository;

import com.agri.insurance.model.InsuredCrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CropRepository extends JpaRepository<InsuredCrop, Long> {
} 