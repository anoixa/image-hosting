/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 160004 (160004)
 Source Host           : localhost:5432
 Source Catalog        : image
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160004 (160004)
 File Encoding         : 65001

 Date: 02/10/2024 23:10:46
*/


-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS "public"."config";
CREATE TABLE "public"."config" (
                                   "config_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                   "user_id" varchar(100) COLLATE "pg_catalog"."default",
                                   "config_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                   "config_value" varchar(255) COLLATE "pg_catalog"."default",
                                   "is_delete" bool NOT NULL DEFAULT false,
                                   "create_time" timestamp(6),
                                   "update_time" timestamp(6)
)
;
COMMENT ON COLUMN "public"."config"."config_id" IS '配置ID';
COMMENT ON COLUMN "public"."config"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."config"."config_key" IS '键名';
COMMENT ON COLUMN "public"."config"."config_value" IS '键值';
COMMENT ON COLUMN "public"."config"."is_delete" IS '是否删除';
COMMENT ON COLUMN "public"."config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."config"."update_time" IS '更新时间';

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO "public"."config" VALUES ('1', NULL, 'ORIGINAL_IMAGE_PROTECTION', '1', 'f', '2024-10-01 18:35:59', '2024-10-01 18:36:01');
INSERT INTO "public"."config" VALUES ('2', NULL, 'ORIGINAL_WEBP_CONVERSION', '1', 'f', '2024-10-01 18:36:04', '2024-10-01 18:36:06');

-- ----------------------------
-- Table structure for image_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."image_data";
CREATE TABLE "public"."image_data" (
                                       "image_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                       "content_type" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
                                       "minio_url" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "minio_key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "user_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                       "file_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                       "size" int4 NOT NULL,
                                       "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
                                       "update_time" timestamp(6) ,
                                       "description" text COLLATE "pg_catalog"."default",
                                       "is_public" bool NOT NULL DEFAULT true,
                                       "is_delete" bool NOT NULL DEFAULT false,
                                       "width" int4,
                                       "height" int4
);

COMMENT ON COLUMN "public"."image_data"."image_id" IS '主键';
COMMENT ON COLUMN "public"."image_data"."content_type" IS '图片类型';
COMMENT ON COLUMN "public"."image_data"."minio_url" IS 'MinIO生成的Url';
COMMENT ON COLUMN "public"."image_data"."minio_key" IS 'MinIO存储Key';
COMMENT ON COLUMN "public"."image_data"."user_id" IS '上传用户ID';
COMMENT ON COLUMN "public"."image_data"."file_name" IS '文件名';
COMMENT ON COLUMN "public"."image_data"."size" IS '文件大小';
COMMENT ON COLUMN "public"."image_data"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."image_data"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."image_data"."description" IS '介绍';
COMMENT ON COLUMN "public"."image_data"."is_public" IS '是否公开';
COMMENT ON COLUMN "public"."image_data"."is_delete" IS '是否删除';
COMMENT ON COLUMN "public"."image_data"."width" IS '宽';
COMMENT ON COLUMN "public"."image_data"."height" IS '高';


-- ----------------------------
-- Records of image_data
-- ----------------------------

-- ----------------------------
-- Table structure for strategies
-- ----------------------------
DROP TABLE IF EXISTS "public"."strategies";
CREATE TABLE "public"."strategies" (
                                       "id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "user_id" varchar(255) COLLATE "pg_catalog"."default",
                                       "config" json NOT NULL,
                                       "create_time" timestamp(6),
                                       "update_time" timestamp(6),
                                       "is_delete" bool,
                                       "type" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."strategies"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."strategies"."update_time" IS '更新时间';

-- ----------------------------
-- Records of strategies
-- ----------------------------
INSERT INTO "public"."strategies" VALUES ('1', NULL, '{"path": "D:\\photo"}', '2024-10-02 20:51:05', '2024-10-02 20:51:07', 'f', '1');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_info";
CREATE TABLE "public"."user_info" (
                                      "user_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                      "user_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                      "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                      "user_email" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                      "create_time" timestamp(6),
                                      "update_time" timestamp(6),
                                      "user_role" varchar(20) COLLATE "pg_catalog"."default",
                                      "is_delete" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."user_info"."user_id" IS '主键';
COMMENT ON COLUMN "public"."user_info"."user_name" IS '用户名';
COMMENT ON COLUMN "public"."user_info"."password" IS '密码';
COMMENT ON COLUMN "public"."user_info"."user_email" IS '用户邮箱';
COMMENT ON COLUMN "public"."user_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."user_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."user_info"."user_role" IS '用户角色';
COMMENT ON COLUMN "public"."user_info"."is_delete" IS '是否删除';

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO "public"."user_info" VALUES ('1', '1', '$2a$10$Vzy6bRdmGtkRpVNoOF7frOtTBBsjnX.GbaEMDOslT0lD/TdHW3Qba', '1', '2024-09-01 01:30:45', '2024-09-01 01:30:47', '1', 't');
INSERT INTO "public"."user_info" VALUES ('1832776654893150209', 'anoixa', '$2a$10$2cBKV1on5szQTcA8/tb57uyRW9bS47KfYrfRFEjL1FxuR./m6IpGy', 'Kw0vxgwu+bn3Ue5I6aWy5w5p82TVZSflrr7DH1TSOCo=', '2024-09-08 21:43:00.52863', NULL, 'admin', 'f');

-- ----------------------------
-- Indexes structure for table config
-- ----------------------------
CREATE INDEX "config_is_delete_index" ON "public"."config" USING btree (
                                                                        "is_delete" "pg_catalog"."bool_ops" ASC NULLS LAST
    );
CREATE INDEX "config_key_index" ON "public"."config" USING btree (
                                                                  "config_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table config
-- ----------------------------
ALTER TABLE "public"."config" ADD CONSTRAINT "config_pkey" PRIMARY KEY ("config_id");

-- ----------------------------
-- Indexes structure for table image_data
-- ----------------------------
CREATE INDEX "image_data_user_id_index" ON "public"."image_data" USING btree (
                                                                              "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
-- Index on minio_key (for efficient retrieval by storage key)
CREATE INDEX "image_data_minio_key_index" ON "public"."image_data" USING btree (
                                                                                "minio_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- Index on is_public (for filtering public/private images)
CREATE INDEX "image_data_is_public_index" ON "public"."image_data" USING btree (
                                                                                "is_public" ASC NULLS LAST
    );

-- Composite index on user_id and is_public (for common filtering)
CREATE INDEX "image_data_user_id_is_public_index" ON "public"."image_data" USING btree (
                                                                                        "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                        "is_public" ASC NULLS LAST
    );
-- Index on is_delete (for efficient filtering of deleted/non-deleted images)
CREATE INDEX "image_data_is_delete_index" ON "public"."image_data" USING btree (
                                                                                "is_delete" ASC NULLS LAST
    );

-- Composite index on user_id and is_delete (for common filtering)
CREATE INDEX "image_data_user_id_is_delete_index" ON "public"."image_data" USING btree (
                                                                                        "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                        "is_delete" ASC NULLS LAST
    );

-- Composite index on is_public and is_delete (for common filtering)
CREATE INDEX "image_data_public_is_delete_index" ON "public"."image_data" USING btree (
                                                                                       "is_public" ASC NULLS LAST,
                                                                                       "is_delete" ASC NULLS LAST
    );
-- ----------------------------
-- Primary Key structure for table image_data
-- ----------------------------
ALTER TABLE "public"."image_data" ADD CONSTRAINT "image_data_pkey" PRIMARY KEY ("image_id");

-- ----------------------------
-- Indexes structure for table strategies
-- ----------------------------
CREATE INDEX "strategies_is_delete_index" ON "public"."strategies" USING btree (
                                                                                "is_delete" "pg_catalog"."bool_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table strategies
-- ----------------------------
ALTER TABLE "public"."strategies" ADD CONSTRAINT "strategies_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table user_info
-- ----------------------------
CREATE INDEX "is_delete_index" ON "public"."user_info" USING btree (
                                                                    "is_delete" "pg_catalog"."bool_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Uniques structure for table user_info
-- ----------------------------
ALTER TABLE "public"."user_info" ADD CONSTRAINT "user_info_user_name_key" UNIQUE ("user_name");
ALTER TABLE "public"."user_info" ADD CONSTRAINT "user_info_user_email_key" UNIQUE ("user_email");

-- ----------------------------
-- Primary Key structure for table user_info
-- ----------------------------
ALTER TABLE "public"."user_info" ADD CONSTRAINT "user_info_pkey" PRIMARY KEY ("user_id");
