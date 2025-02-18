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