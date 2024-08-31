-- 用户表 (User Table)
DROP TABLE IF EXISTS user_info;
CREATE TABLE user_info (
    user_id VARCHAR(100) PRIMARY KEY, 
    user_name VARCHAR(50) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL,  
    user_email VARCHAR(100) UNIQUE NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP, 
    user_role VARCHAR(20),  
    is_delete BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON COLUMN user_info.user_id IS '主键';
COMMENT ON COLUMN user_info.user_name IS '用户名';
COMMENT ON COLUMN user_info.password IS '密码';
COMMENT ON COLUMN user_info.user_email IS '用户邮箱';
COMMENT ON COLUMN user_info.user_role IS '用户角色';
COMMENT ON COLUMN user_info.create_time IS '创建时间';
COMMENT ON COLUMN user_info.update_time IS '更新时间';
COMMENT ON COLUMN user_info.is_delete IS '是否删除';

-- 图片表 (Images Table)
DROP TABLE IF EXISTS image_data;
CREATE TABLE image_data (
    image_id VARCHAR(100) PRIMARY KEY,
    image_type VARCHAR(10) NOT NULL,
    image_url VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    source_type VARCHAR(10) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(100) NOT NULL,
    file_size INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,  
    is_public BOOLEAN NOT NULL DEFAULT TRUE, 
    is_delete BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON COLUMN image_data.image_id IS '主键';
COMMENT ON COLUMN image_data.image_url IS '生成的图片Url';
COMMENT ON COLUMN image_data.image_type IS '图片类型';
COMMENT ON COLUMN image_data.user_id IS '上传用户ID';
COMMENT ON COLUMN image_data.source_type IS '储存类型';
COMMENT ON COLUMN image_data.file_path IS '文件路径';
COMMENT ON COLUMN image_data.file_name IS '文件名';
COMMENT ON COLUMN image_data.file_size IS '文件大小';
COMMENT ON COLUMN image_data.create_time IS '创建时间';
COMMENT ON COLUMN image_data.description IS '介绍';
COMMENT ON COLUMN image_data.is_public IS '是否公开';
COMMENT ON COLUMN image_data.is_delete IS '是否删除';

CREATE INDEX image_data_user_id_index ON image_data (user_id);

-- 配置表 (Config Table)
DROP TABLE IF EXISTS config;
CREATE TABLE config (
    config_id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100),
    config_key VARCHAR(50) NOT NULL,
    config_value VARCHAR(255)
);

COMMENT ON COLUMN config.config_id IS '配置ID';
COMMENT ON COLUMN config.user_id IS '用户ID';
COMMENT ON COLUMN config.config_key IS '键名';
COMMENT ON COLUMN config.config_value IS '键值';

CREATE INDEX config_user_id_index ON config (user_id);
CREATE INDEX config_key_index ON config (config_key);