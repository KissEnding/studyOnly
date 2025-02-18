package com.agri.insurance.repository;

import com.agri.insurance.model.Farmer;
import com.agri.insurance.model.InsuranceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceOrderRepository extends JpaRepository<InsuranceOrder, Long> {
    
    @Query("SELECT o FROM InsuranceOrder o LEFT JOIN FETCH o.farmer LEFT JOIN FETCH o.product WHERE o.product.company.username = :username AND o.status = :status ORDER BY o.createTime DESC")
    List<InsuranceOrder> findByCompanyUsernameAndStatusOrderByCreateTimeDesc(String username, String status);

    @Query("SELECT o FROM InsuranceOrder o LEFT JOIN FETCH o.farmer LEFT JOIN FETCH o.product WHERE o.product.company.username = :username ORDER BY o.createTime DESC")
    List<InsuranceOrder> findByCompanyUsernameOrderByCreateTimeDesc(String username);
    
    @Query("SELECT o FROM InsuranceOrder o LEFT JOIN FETCH o.product WHERE o.farmer = :farmer")
    List<InsuranceOrder> findByFarmer(Farmer farmer);
    
    @Query("SELECT o FROM InsuranceOrder o LEFT JOIN FETCH o.product WHERE o.farmer = :farmer AND o.status = :status")
    List<InsuranceOrder> findByFarmerAndStatus(Farmer farmer, String status);
    
    @Query("SELECT o FROM InsuranceOrder o LEFT JOIN FETCH o.product WHERE o.id = :id AND o.farmer = :farmer")
    Optional<InsuranceOrder> findByIdAndFarmer(Long id, Farmer farmer);
} 