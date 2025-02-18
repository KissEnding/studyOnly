ALTER TABLE insurance_product ADD COLUMN user_type ENUM('new', 'professional', 'company', 'all') DEFAULT 'all' COMMENT '目标用户类型：new-新型农民，professional-专业农户，company-农业企业，all-所有用户';
