package com.agri.insurance.service;

import com.agri.insurance.model.*;
import com.agri.insurance.repository.*;
import com.agri.insurance.dto.OrderAuditRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceOrderService {
    private final InsuranceOrderRepository orderRepository;
    private final InsurancePolicyRepository policyRepository;
    private final FarmerRepository farmerRepository;
    private final InsuranceProductRepository productRepository;
    private final InsuranceCompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<InsuranceOrder> getOrders(String username, String status) {
        Farmer farmer = farmerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (StringUtils.hasText(status)) {
            return orderRepository.findByFarmerAndStatus(farmer, status);
        }
        return orderRepository.findByFarmer(farmer);
    }

    @Transactional(readOnly = true)
    public List<InsuranceOrder> getCompanyOrders(String username, String status) {
        if (StringUtils.hasText(status)) {
            return orderRepository.findByCompanyUsernameAndStatusOrderByCreateTimeDesc(username, status);
        }
        return orderRepository.findByCompanyUsernameOrderByCreateTimeDesc(username);
    }

    @Transactional(readOnly = true)
    public InsuranceOrder getOrder(Long id, String username) {
        Farmer farmer = farmerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return orderRepository.findByIdAndFarmer(id, farmer)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public InsuranceOrder createOrder(Long productId, String username, Double area) {
        Farmer farmer = farmerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        InsuranceProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        InsuranceOrder order = new InsuranceOrder();
        order.setFarmer(farmer);
        order.setProduct(product);
        order.setArea(BigDecimal.valueOf(area));
        order.setStatus("PENDING");
        order.setOrderCode(generateOrderCode());
        
        // Calculate premium and coverage based on area
        BigDecimal premium = product.getBasePremium().multiply(BigDecimal.valueOf(area));
        BigDecimal coverage = premium.multiply(product.getCoverageMultiplier());
        
        order.setPremium(premium);
        order.setCoverage(coverage);
        
        // 设置创建时间和更新时间
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    @Transactional
    public void auditOrder(Long id, OrderAuditRequest request, String username) {
        InsuranceOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getCompanyUsername().equals(username)) {
            throw new RuntimeException("No permission to audit this order");
        }

        if ("approve".equals(request.getAction())) {
            order.setStatus("approved");
            // 生成保险单
            createPolicy(order);
        } else if ("reject".equals(request.getAction())) {
            order.setStatus("rejected");
        } else {
            throw new RuntimeException("Invalid audit action");
        }

        order.setAuditRemarks(request.getRemarks());
        order.setAuditTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    private void createPolicy(InsuranceOrder order) {
        // 生成保单号 POL + 年月日 + 4位随机数
        String policyCode = "POL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", (int)(Math.random() * 10000));

        InsurancePolicy policy = InsurancePolicy.builder()
                .policyCode(policyCode)
                .orderId(order.getId())
                .actualArea(order.getArea())
                .unitPrice(order.getProduct().getBasePremium())
                .originalPremium(order.getPremium())
                .actualPremium(order.getPremium())  // 如果没有优惠，实际保费等于原始保费
                .startDate(LocalDate.now())
                .endDate(order.getProduct().getEndDate())
                .paymentStatus("unpaid")  // 初始状态为未支付
                .farmerId(order.getFarmer().getId())
                .companyId(order.getProduct().getCompany().getId())
                .productId(order.getProduct().getId())
                .cropType(order.getProduct().getCropType())
                .discountRate(BigDecimal.ONE)  // 如果没有优惠，折扣率为1
                .status("active")
                .build();

        policyRepository.save(policy);
        log.info("Created policy {} for order {}", policyCode, order.getOrderCode());
    }

    private String generateOrderCode() {
        // 生成订单号 ORD + 年月日 + 4位随机数
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", (int)(Math.random() * 10000));
    }
} 