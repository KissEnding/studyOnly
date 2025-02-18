CREATE TABLE premium_tier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    min_area DECIMAL(10,2) NOT NULL COMMENT '最小面积（亩）',
    max_area DECIMAL(10,2) COMMENT '最大面积（亩），null表示无上限',
    discount_rate DECIMAL(3,2) NOT NULL COMMENT '折扣率',
    FOREIGN KEY (product_id) REFERENCES insurance_product(id) ON DELETE CASCADE
) COMMENT='保费阶梯定价表'; 