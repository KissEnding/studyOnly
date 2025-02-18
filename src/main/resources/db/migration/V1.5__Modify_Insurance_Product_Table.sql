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