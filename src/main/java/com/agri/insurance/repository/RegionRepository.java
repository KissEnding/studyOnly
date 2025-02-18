package com.agri.insurance.repository;

import com.agri.insurance.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByLevelOrderByCode(String level);
    List<Region> findByParentCodeOrderByCode(String parentCode);
    Optional<Region> findByCode(String code);
} 