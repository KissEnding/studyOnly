## “农安心”农业保险服务平台

​		我国是农业大国，农业从业人口众多。近年来自然灾害频发，给农民利益造成严重损害。为了保障农民利益，保险公司开设了农业保险业务，目的是为遭受自然灾害的农户提供一定经济保障。目前农业保险已经覆盖到小麦、稻谷、玉米棉花、烟叶等多种农作物。为了方便农民办理保险，保监会拟建设农业保险公共服务平台。
### 【数据库设计】

#### 1. 概念模型设计

##### 1.保险公司(insurance_company)

- 公司ID (id) - PK
- 公司名称 (company_name)
- 统一征信代码 (credit_code)
- 联系人 (contact_person)
- 联系方式 (contact_phone)
- 公司所在地 (location)
- 公司类型 (type) [总公司/分公司]
- 所属总公司ID (parent_id) - FK
- 登录用户名 (username)
- 登录密码 (password)
- 审核状态 (status)
- 注册时间 (created_at)

##### 2.农户(farmer)

- 农户ID (id) - PK

- 户主姓名 (name)

- 身份证号码 (id_card)

- 户籍所在地 (location)

- 登录用户名 (username)

- 登录密码 (password)

- 户口簿扫描件 (household_doc)

- 注册时间 (created_at)

##### 3.保险产品(insurance_product)

- 产品ID (id) - PK
- 保险公司ID (company_id) - FK
- 产品编号 (product_code)
- 产品名称 (product_name)
- 面向地区 (target_area)
- 保险农作物 (crop_type)
- 保险开始日期 (start_date)
- 保险结束日期 (end_date)
- 库存额度 (stock_amount)
- 基础保费(每亩) (base_premium)
- 可见状态 (visible)
- 创建时间 (created_at)

##### 4.保费阶梯(premium_ladder)

- ID (id) - PK
- 产品ID (product_id) - FK
- 起始面积 (min_area)
- 折扣率 (discount_rate)

##### 5.投保订单(insurance_order)

- 订单ID (id) - PK
- 农户ID (farmer_id) - FK
- 产品ID (product_id) - FK
- 投保面积 (area)
- 联系方式 (contact_phone)
- 联系人 (contact_person)
- 订单状态 (status)
- 创建时间 (created_at)

##### 6.保险单(insurance_policy)

- 保单ID (id) - PK
- 订单ID (order_id) - FK
- 实际投保面积 (actual_area)
- 每亩单价 (unit_price)
- 应付保费 (original_premium)
- 实付保费 (actual_premium)
- 付款状态 (payment_status)
- 创建时间 (created_at)

##### 7.赔付记录(compensation_record)

- 赔付ID (id) - PK
- 保单ID (policy_id) - FK
- 灾害发生时间 (disaster_time)
- 灾害类型 (disaster_type)
- 受损面积 (damaged_area)
- 应赔付金额 (compensation_amount)
- 赔付状态 (status)
- 赔付日期 (payment_date)
- 创建时间 (created_at)

##### 8.产品评价(product_review)

- 评价ID (id) - PK
- 农户ID (farmer_id) - FK
- 产品ID (product_id) - FK
- 评分 (rating)
- 评价内容 (content)
- 评价时间 (created_at)

#### 2. 逻辑结构设计

#### 3.物理结构设计

#### 4. 数据库实施

```mysql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS agri_insurance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE agri_insurance;

-- 保险公司表
CREATE TABLE insurance_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(100) NOT NULL,
    credit_code VARCHAR(50) NOT NULL UNIQUE,
    contact_person VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    location VARCHAR(200) NOT NULL,
    type ENUM('headquarters', 'branch') NOT NULL,
    parent_id BIGINT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES insurance_company(id)
);

-- 农户表
CREATE TABLE farmer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    location VARCHAR(200) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    household_doc VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 保险产品表
CREATE TABLE insurance_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    target_area VARCHAR(200) NOT NULL,
    crop_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    stock_amount DECIMAL(15,2) NOT NULL,
    base_premium DECIMAL(10,2) NOT NULL,
    visible BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES insurance_company(id)
);
-- 编号规则可以设计为：AGRI-{公司编号}-{年月}-{序号}，例如：AGRI-001-202403-0001


-- 保费阶梯表
CREATE TABLE premium_ladder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    min_area DECIMAL(10,2) NOT NULL,
    discount_rate DECIMAL(4,2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES insurance_product(id)
);

-- 投保订单表
CREATE TABLE insurance_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    farmer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    area DECIMAL(10,2) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    contact_person VARCHAR(50) NOT NULL,
    status ENUM('pending', 'verified', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES farmer(id),
    FOREIGN KEY (product_id) REFERENCES insurance_product(id)
);

-- 保险单表
CREATE TABLE insurance_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL UNIQUE,
    actual_area DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    original_premium DECIMAL(12,2) NOT NULL,
    actual_premium DECIMAL(12,2) NOT NULL,
    payment_status BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES insurance_order(id)
);

-- 赔付记录表
CREATE TABLE compensation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    policy_id BIGINT NOT NULL,
    disaster_time DATETIME NOT NULL,
    disaster_type VARCHAR(50) NOT NULL,
    damaged_area DECIMAL(10,2) NOT NULL,
    compensation_amount DECIMAL(12,2) NOT NULL,
    status BOOLEAN DEFAULT false,
    payment_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES insurance_policy(id)
);

--添加农作物表
CREATE TABLE insured_crop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crop_code VARCHAR(20) NOT NULL UNIQUE COMMENT '农作物编码',
    crop_name VARCHAR(50) NOT NULL COMMENT '农作物名称',
    crop_category VARCHAR(20) NOT NULL COMMENT '作物类别(粮食/经济/果树等)',
    description TEXT COMMENT '描述',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) COMMENT='保险农作物表';


-- 创建产品-农作物关联表
CREATE TABLE product_crop (
    product_id BIGINT NOT NULL,
    crop_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, crop_id),
    FOREIGN KEY (product_id) REFERENCES insurance_product(id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES insured_crop(id) ON DELETE CASCADE
) COMMENT='保险产品-农作物关联表';

-- 修改保险产品表的地区字段
ALTER TABLE insurance_product 
    DROP COLUMN target_area,
    ADD COLUMN province_code VARCHAR(6) COMMENT '省份编码',
    ADD COLUMN city_code VARCHAR(6) COMMENT '城市编码',
    ADD COLUMN district_code VARCHAR(6) COMMENT '区县编码';

-- 插入一些基础数据
INSERT INTO insured_crop (crop_code, crop_name, crop_category, description) VALUES
('RICE', '水稻', '粮食作物', '包括早稻、中稻和晚稻'),
('WHEAT', '小麦', '粮食作物', '包括冬小麦和春小麦'),
('CORN', '玉米', '粮食作物', '包括春玉米和夏玉米'),
('COTTON', '棉花', '经济作物', '棉花及其制品'),
('SOYBEAN', '大豆', '经济作物', '大豆及其制品'),
('RAPESEED', '油菜', '经济作物', '油菜及其制品'),
('APPLE', '苹果', '果树', '苹果树及其果实'),
('ORANGE', '柑橘', '果树', '柑橘树及其果实'),
('GRAPE', '葡萄', '果树', '葡萄树及其果实'),
('TEA', '茶叶', '经济作物', '茶树及其制品');

-- 产品评价表
CREATE TABLE product_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    farmer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES farmer(id),
    FOREIGN KEY (product_id) REFERENCES insurance_product(id)
);

-- 创建管理员表
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- 创建地区表
CREATE TABLE region (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    level VARCHAR(20) NOT NULL,  -- province, city, district
    parent_code VARCHAR(20),
    INDEX idx_parent_code (parent_code)
);

-- 插入初始管理员账号 (密码是 admin123 的加密形式)
INSERT INTO admin (username, password, name) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyUor7IKm', '系统管理员');

-- 添加触发器存储过程

-- 1. 评价时间限制触发器
DELIMITER //
CREATE TRIGGER before_insert_review
BEFORE INSERT ON product_review
FOR EACH ROW
BEGIN
    DECLARE policy_end_date DATE;
    DECLARE policy_exists INT;
    
    -- 检查是否存在有效保单
    SELECT COUNT(*), MAX(ip.end_date)
    INTO policy_exists, policy_end_date
    FROM insurance_policy p
    JOIN insurance_order o ON p.order_id = o.id
    JOIN insurance_product ip ON o.product_id = ip.id
    WHERE o.farmer_id = NEW.farmer_id 
    AND o.product_id = NEW.product_id;
    
    -- 验证评价时间是否在有效期内
    IF policy_exists = 0 OR 
       CURRENT_DATE < policy_end_date OR 
       CURRENT_DATE > DATE_ADD(policy_end_date, INTERVAL 6 MONTH) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '评价时间不在允许范围内';
    END IF;
END //
DELIMITER ;

-- 2. 保险产品库存更新触发器
DELIMITER //
CREATE TRIGGER after_policy_insert
AFTER INSERT ON insurance_policy
FOR EACH ROW
BEGIN
    UPDATE insurance_product ip
    JOIN insurance_order io ON io.product_id = ip.id
    SET ip.stock_amount = ip.stock_amount - NEW.actual_premium
    WHERE io.id = NEW.order_id;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER after_policy_cancel
AFTER UPDATE ON insurance_policy
FOR EACH ROW
BEGIN
    IF NEW.status = 'CANCELLED' AND OLD.status != 'CANCELLED' THEN
        UPDATE insurance_product ip
        JOIN insurance_order io ON io.product_id = ip.id
        SET ip.stock_amount = ip.stock_amount + NEW.actual_premium
        WHERE io.id = NEW.order_id;
    END IF;
END //
DELIMITER ;

-- 地区启用/禁用： 禁用父级地区时，禁用所有子级地区。
DELIMITER //
CREATE TRIGGER after_region_update
AFTER UPDATE ON region
FOR EACH ROW
BEGIN
    IF NEW.enabled = b'0' THEN
        UPDATE region
        SET enabled = b'0'
        WHERE parent_code = OLD.code;
    END IF;
END //
DELIMITER ;




-- 3. 计算保费的存储过程
DELIMITER //
CREATE PROCEDURE calculate_premium(
    IN p_area DECIMAL(10,2),
    IN p_product_id BIGINT,
    OUT p_final_premium DECIMAL(12,2)
)
BEGIN
    DECLARE base_price DECIMAL(10,2);
    DECLARE discount_rate DECIMAL(4,2);
    
    -- 获取基础价格
    SELECT base_premium INTO base_price
    FROM insurance_product
    WHERE id = p_product_id;
    
    -- 获取适用的折扣率
    SELECT discount_rate INTO discount_rate
    FROM premium_ladder
    WHERE product_id = p_product_id
    AND min_area <= p_area
    ORDER BY min_area DESC
    LIMIT 1;
    
    IF discount_rate IS NULL THEN
        SET discount_rate = 1.00;
    END IF;
    
    -- 计算最终保费
    SET p_final_premium = p_area * base_price * discount_rate;
END //
DELIMITER ;

-- 4. 农户投保地区验证存储过程
DELIMITER //
CREATE PROCEDURE verify_insurance_eligibility(
    IN p_farmer_id BIGINT,
    IN p_product_id BIGINT,
    OUT p_eligible BOOLEAN
)
BEGIN
    DECLARE farmer_location VARCHAR(200);
    DECLARE product_area VARCHAR(200);
    
    SELECT location INTO farmer_location
    FROM farmer
    WHERE id = p_farmer_id;
    
    SELECT target_area INTO product_area
    FROM insurance_product
    WHERE id = p_product_id;
    
    -- 检查地区是否匹配
    IF farmer_location LIKE CONCAT('%', product_area, '%') OR 
       product_area LIKE CONCAT('%', farmer_location, '%') THEN
        SET p_eligible = TRUE;
    ELSE
        SET p_eligible = FALSE;
    END IF;
END //
DELIMITER ;

-- 5. 添加索引优化查询性能
ALTER TABLE insurance_order ADD INDEX idx_farmer_product (farmer_id, product_id);
ALTER TABLE insurance_policy ADD INDEX idx_created_at (created_at);
ALTER TABLE compensation_record ADD INDEX idx_disaster_time (disaster_time);
ALTER TABLE product_review ADD INDEX idx_product_rating (product_id, rating);

-- 6. 添加视图便于统计分析
CREATE VIEW company_product_stats AS
SELECT 
    ic.id as company_id,
    ic.company_name,
    ic.type,
    COUNT(ip.id) as product_count,
    SUM(CASE WHEN ip.visible = 1 THEN 1 ELSE 0 END) as active_product_count
FROM insurance_company ic
LEFT JOIN insurance_product ip ON ic.id = ip.company_id
GROUP BY ic.id, ic.company_name, ic.type;

CREATE VIEW yearly_farmer_registration AS
SELECT 
    YEAR(created_at) as registration_year,
    COUNT(*) as registration_count,
    COUNT(DISTINCT location) as region_count
FROM farmer
GROUP BY YEAR(created_at);

-- 7. 保险赔付金额计算存储过程
DELIMITER //
CREATE PROCEDURE calculate_compensation(
    IN p_policy_id BIGINT,
    IN p_damaged_area DECIMAL(10,2),
    OUT p_compensation_amount DECIMAL(12,2)
)
BEGIN
    DECLARE total_area DECIMAL(10,2);
    DECLARE unit_price DECIMAL(10,2);
    DECLARE damage_ratio DECIMAL(4,2);
    
    -- 获取保单信息
    SELECT actual_area, unit_price 
    INTO total_area, unit_price
    FROM insurance_policy
    WHERE id = p_policy_id;
    
    -- 计算受损比例
    SET damage_ratio = p_damaged_area / total_area;
    
    -- 计算赔付金额（假设赔付金额 = 受损面积 * 单价 * 1.5的赔付系数）
    SET p_compensation_amount = p_damaged_area * unit_price * 1.5;
    
    -- 如果受损比例超过80%，额外增加10%的赔付
    IF damage_ratio > 0.8 THEN
        SET p_compensation_amount = p_compensation_amount * 1.1;
    END IF;
END //
DELIMITER ;

-- 8. 保险公司产品统计存储过程
DELIMITER //
CREATE PROCEDURE company_statistics(
    IN p_company_id BIGINT,
    OUT p_total_products INT,
    OUT p_total_policies INT,
    OUT p_total_compensation DECIMAL(15,2)
)
BEGIN
    -- 获取产品总数
    SELECT COUNT(*) INTO p_total_products
    FROM insurance_product
    WHERE company_id = p_company_id;
    
    -- 获取保单总数
    SELECT COUNT(p.id) INTO p_total_policies
    FROM insurance_policy p
    JOIN insurance_order o ON p.order_id = o.id
    JOIN insurance_product ip ON o.product_id = ip.id
    WHERE ip.company_id = p_company_id;
    
    -- 获取赔付总额
    SELECT COALESCE(SUM(cr.compensation_amount), 0) INTO p_total_compensation
    FROM compensation_record cr
    JOIN insurance_policy p ON cr.policy_id = p.id
    JOIN insurance_order o ON p.order_id = o.id
    JOIN insurance_product ip ON o.product_id = ip.id
    WHERE ip.company_id = p_company_id
    AND cr.status = true;
END //
DELIMITER ;

-- 9. 订单状态更新触发器
DELIMITER //
CREATE TRIGGER after_policy_status_update
AFTER UPDATE ON insurance_policy
FOR EACH ROW
BEGIN
    IF NEW.payment_status = true AND OLD.payment_status = false THEN
        -- 更新订单状态为已验证
        UPDATE insurance_order
        SET status = 'verified'
        WHERE id = NEW.order_id;
        
        -- 更新产品库存
        UPDATE insurance_product ip
        JOIN insurance_order io ON io.product_id = ip.id
        SET ip.stock_amount = ip.stock_amount - NEW.actual_premium
        WHERE io.id = NEW.order_id;
    END IF;
END //
DELIMITER ;

-- 10. 添加更多统计视图
-- 保险公司赔付统计视图
CREATE VIEW company_compensation_stats AS
SELECT 
    ic.id as company_id,
    ic.company_name,
    COUNT(DISTINCT cr.id) as compensation_count,
    SUM(cr.compensation_amount) as total_compensation,
    AVG(cr.compensation_amount) as avg_compensation
FROM insurance_company ic
JOIN insurance_product ip ON ic.id = ip.company_id
JOIN insurance_order io ON ip.id = io.product_id
JOIN insurance_policy ipl ON io.id = ipl.order_id
LEFT JOIN compensation_record cr ON ipl.id = cr.policy_id
WHERE cr.status = true
GROUP BY ic.id, ic.company_name;

-- 农作物保险统计视图
CREATE VIEW crop_insurance_stats AS
SELECT 
    ip.crop_type,
    COUNT(DISTINCT ip.id) as product_count,
    COUNT(DISTINCT io.id) as order_count,
    AVG(ip.base_premium) as avg_premium,
    SUM(cr.compensation_amount) as total_compensation
FROM insurance_product ip
LEFT JOIN insurance_order io ON ip.id = io.product_id
LEFT JOIN insurance_policy ipl ON io.id = ipl.order_id
LEFT JOIN compensation_record cr ON ipl.id = cr.policy_id
GROUP BY ip.crop_type;

-- 11. 添加数据一致性检查存储过程
DELIMITER //
CREATE PROCEDURE check_data_consistency()
BEGIN
    DECLARE error_message VARCHAR(1000);
    SET error_message = '';
    
    -- 检查保单金额是否超过产品库存
    IF EXISTS (
        SELECT 1 
        FROM insurance_product ip
        WHERE stock_amount < 0
    ) THEN
        SET error_message = CONCAT(error_message, '存在产品库存为负数；');
    END IF;
    
    -- 检查赔付金额是否超过保单金额
    IF EXISTS (
        SELECT 1
        FROM insurance_policy ip
        JOIN compensation_record cr ON ip.id = cr.policy_id
        GROUP BY ip.id
        HAVING SUM(cr.compensation_amount) > (ip.actual_premium * 2)
    ) THEN
        SET error_message = CONCAT(error_message, '存在赔付金额超过保单金额两倍的记录；');
    END IF;
    
    -- 如果发现错误，抛出异常
    IF error_message != '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = error_message;
    END IF;
END //
DELIMITER ;

-- 12. 定期执行数据一致性检查的事件
CREATE EVENT check_data_consistency_event
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
    CALL check_data_consistency(); 

-- 13. 添加操作日志表
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    operator_type ENUM('admin', 'company', 'farmer') NOT NULL,
    operator_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_content TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. 添加操作日志触发器
DELIMITER //
CREATE TRIGGER after_policy_change
AFTER UPDATE ON insurance_policy
FOR EACH ROW
BEGIN
    INSERT INTO operation_log (operator_type, operator_id, operation_type, operation_content)
    VALUES ('company', NEW.id, 'update_policy', 
        CONCAT('Policy ID: ', NEW.id, 
               ', Status changed from ', IF(OLD.payment_status, 'paid', 'unpaid'),
               ' to ', IF(NEW.payment_status, 'paid', 'unpaid')));
END //
DELIMITER ;

-- 15. 添加数据备份存储过程
DELIMITER //
CREATE PROCEDURE backup_important_data()
BEGIN
    -- 创建备份表（如果不存在）
    CREATE TABLE IF NOT EXISTS insurance_policy_backup LIKE insurance_policy;
    CREATE TABLE IF NOT EXISTS compensation_record_backup LIKE compensation_record;
    
    -- 备份当天的数据
    INSERT INTO insurance_policy_backup
    SELECT * FROM insurance_policy
    WHERE DATE(created_at) = CURRENT_DATE;
    
    INSERT INTO compensation_record_backup
    SELECT * FROM compensation_record
    WHERE DATE(created_at) = CURRENT_DATE;
END //
DELIMITER ;

-- 16. 添加数据归档存储过程
DELIMITER //
CREATE PROCEDURE archive_old_data(IN p_months INT)
BEGIN
    -- 创建归档表（如果不存在）
    CREATE TABLE IF NOT EXISTS archived_reviews LIKE product_review;
    CREATE TABLE IF NOT EXISTS archived_compensation LIKE compensation_record;
    
    -- 归档老数据
    INSERT INTO archived_reviews
    SELECT * FROM product_review
    WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL p_months MONTH);
    
    INSERT INTO archived_compensation
    SELECT * FROM compensation_record
    WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL p_months MONTH);
    
    -- 删除已归档的数据
    DELETE FROM product_review
    WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL p_months MONTH);
    
    DELETE FROM compensation_record
    WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL p_months MONTH);
END //
DELIMITER ;

-- 17. 添加农户信用评分表
CREATE TABLE farmer_credit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    farmer_id BIGINT NOT NULL,
    credit_score INT DEFAULT 100,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES farmer(id)
);

-- 18. 添加信用评分更新触发器
DELIMITER //
CREATE TRIGGER after_compensation_insert
AFTER INSERT ON compensation_record
FOR EACH ROW
BEGIN
    DECLARE v_farmer_id BIGINT;
    
    -- 获取农户ID
    SELECT f.id INTO v_farmer_id
    FROM farmer f
    JOIN insurance_order io ON f.id = io.farmer_id
    JOIN insurance_policy ip ON io.id = ip.order_id
    WHERE ip.id = NEW.policy_id;
    
    -- 更新信用评分（假设每次赔付降低5分）
    UPDATE farmer_credit
    SET credit_score = GREATEST(credit_score - 5, 0),
        last_updated = CURRENT_TIMESTAMP
    WHERE farmer_id = v_farmer_id;
END //
DELIMITER ;

-- 补充代码（删除赔付记录时恢复信用评分）
DELIMITER //
CREATE TRIGGER after_compensation_delete
AFTER DELETE ON compensation_record
FOR EACH ROW
BEGIN
    DECLARE v_farmer_id BIGINT;
    
    SELECT f.id INTO v_farmer_id
    FROM farmer f
    JOIN insurance_order io ON f.id = io.farmer_id
    JOIN insurance_policy ip ON io.id = ip.order_id
    WHERE ip.id = OLD.policy_id;
    
    UPDATE farmer_credit
    SET credit_score = LEAST(credit_score + 5, 100),
        last_updated = CURRENT_TIMESTAMP
    WHERE farmer_id = v_farmer_id;
END //
DELIMITER ;
-- 补充代码（更新保单状态）
DELIMITER //
CREATE TRIGGER after_compensation_status_update
AFTER UPDATE ON compensation_record
FOR EACH ROW
BEGIN
    IF NEW.status = 1 AND OLD.status = 0 THEN
        UPDATE insurance_policy
        SET status = 'COMPENSATED'
        WHERE id = NEW.policy_id;
    END IF;
END //
DELIMITER ;


-- 19. 添加地区风险评估视图
CREATE VIEW area_risk_assessment AS
SELECT 
    ip.target_area,
    COUNT(DISTINCT cr.id) as disaster_count,
    SUM(cr.damaged_area) as total_damaged_area,
    SUM(cr.compensation_amount) as total_compensation,
    COUNT(DISTINCT ip.id) as product_count,
    AVG(ip.base_premium) as avg_premium
FROM insurance_product ip
LEFT JOIN insurance_order io ON ip.id = io.product_id
LEFT JOIN insurance_policy ipl ON io.id = ipl.order_id
LEFT JOIN compensation_record cr ON ipl.id = cr.policy_id
GROUP BY ip.target_area;

-- 20. 添加农作物季节性价格调整存储过程
DELIMITER //
CREATE PROCEDURE adjust_seasonal_premium(
    IN p_crop_type VARCHAR(50),
    IN p_adjustment_rate DECIMAL(4,2)
)
BEGIN
    -- 更新指定农作物的基础保费
    UPDATE insurance_product
    SET base_premium = base_premium * p_adjustment_rate
    WHERE crop_type = p_crop_type
    AND visible = true;
    
    -- 记录价格调整日志
    INSERT INTO operation_log (
        operator_type, 
        operator_id, 
        operation_type, 
        operation_content
    )
    VALUES (
        'admin',
        1, -- 假设系统管理员ID为1
        'price_adjustment',
        CONCAT('Adjusted premium for ', p_crop_type, ' by rate: ', p_adjustment_rate)
    );
END //
DELIMITER ;

-- 21. 创建定时任务
CREATE EVENT backup_daily_data
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
    CALL backup_important_data();

CREATE EVENT monthly_data_archive
ON SCHEDULE EVERY 1 MONTH
STARTS CURRENT_TIMESTAMP
DO
    CALL archive_old_data(12); -- 归档12个月前的数据

-- 22. 添加数据库事件调度器开关
SET GLOBAL event_scheduler = ON; 


-- 23. premium_ladder 表的事务处理：折扣阶梯更新事务：更新 premium_ladder 时需确保关联的保单和产品信息一致性。
DELIMITER //
CREATE PROCEDURE update_premium_ladder_transaction(
    IN p_product_id BIGINT,
    IN p_min_area DECIMAL(10,2),
    IN p_discount_rate DECIMAL(4,2)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Transaction failed during premium ladder update';
    END;

    START TRANSACTION;

    -- 更新折扣阶梯
    UPDATE premium_ladder
    SET discount_rate = p_discount_rate
    WHERE product_id = p_product_id AND min_area = p_min_area;

    -- 更新所有受影响的保单
    UPDATE insurance_policy ip
    JOIN insurance_product prod ON ip.product_id = prod.id
    SET ip.actual_premium = ip.actual_area * prod.base_premium * p_discount_rate
    WHERE prod.id = p_product_id;

    COMMIT;
END //
DELIMITER ;

-- 24. archived_compensation 表的事务处理：归档事务： 将历史记录从 compensation_record 移动到 archived_compensation，并删除原表中的对应数据。
DELIMITER //
CREATE PROCEDURE archive_compensation_records(IN p_months INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Failed to archive compensation records';
    END;

    START TRANSACTION;

    -- 归档数据到 archived_compensation
    INSERT INTO archived_compensation
    SELECT * FROM compensation_record
    WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL p_months MONTH);

    -- 删除主表中的已归档数据
    DELETE FROM compensation_record
    WHERE created_at < DATE_SUB(CURRENT_DATE, INTERVAL p_months MONTH);

    COMMIT;
END //
DELIMITER ;


-- compensation_record 表补充触发器（赔付状态更新）：
DELIMITER //
CREATE TRIGGER after_compensation_status_update
AFTER UPDATE ON compensation_record
FOR EACH ROW
BEGIN
    IF NEW.status = 1 AND OLD.status = 0 THEN
        UPDATE insurance_policy
        SET status = 'COMPENSATED'
        WHERE id = NEW.policy_id;
    END IF;
END //
DELIMITER ;


-- 修改 farmer 表，添加 status 字段
ALTER TABLE farmer
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'pending' 
COMMENT '审核状态：pending-待审核，approved-已通过，rejected-已拒绝';

```

### 启动

后端

```
# 进入后端项目根目录
cd agri-insurance

# 使用Maven编译并启动
mvn spring-boot:run

```

前端

```
# 进入前端项目目录
cd agri-insurance-frontend

# 安装依赖
npm install
# 或者
yarn install

# 启动开发服务器
npm run serve
# 或者
yarn serve
```



```
{
    "results": [
        {
            "location": {
                "id": "WTMKQ069CCJ7",
                "name": "杭州",
                "country": "CN",
                "path": "杭州,杭州,浙江,中国",
                "timezone": "Asia/Shanghai",
                "timezoneOffset": null
            },
            "daily": [
                {
                    "date": "2024-12-31",
                    "textDay": null,
                    "codeDay": null,
                    "textNight": null,
                    "codeNight": null,
                    "high": "11",
                    "low": "2",
                    "rainfall": "0.00",
                    "precip": "0.00",
                    "windDirection": null,
                    "windDirectionDegree": null,
                    "windSpeed": null,
                    "windScale": null,
                    "humidity": "60"
                }
            ],
            "lastUpdate": null
        }
    ]
}
```

