-- ============================================================
-- 云枢智学 — 数据库初始化脚本（MySQL 8.0+）
-- 字符集：utf8mb4  排序规则：utf8mb4_unicode_ci
-- 引擎：InnoDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS `yunshu_db`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `yunshu_db`;

-- ============================================================
-- 1. 后台管理员账号表
-- ============================================================
CREATE TABLE `admin_user` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`      VARCHAR(64)      NOT NULL                COMMENT '登录用户名（唯一）',
    `phone`         VARCHAR(20)               DEFAULT NULL   COMMENT '手机号（唯一，脱敏展示用）',
    `email`         VARCHAR(128)             DEFAULT NULL   COMMENT '邮箱（可选）',
    `password_hash` VARCHAR(255)     NOT NULL                COMMENT 'BCrypt 加密后的密码',
    `avatar_url`    VARCHAR(512)             DEFAULT NULL   COMMENT '头像 URL',
    `role_id`       BIGINT UNSIGNED  NOT NULL                COMMENT '角色 ID（关联 sys_role.id）',
    `status`        TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `last_login_at` DATETIME                  DEFAULT NULL   COMMENT '最后登录时间（CST）',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_role_status` (`role_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理员账号';

-- 内置超级管理员初始数据（密码需首次登录后强制修改）
INSERT INTO `admin_user` (`username`, `phone`, `password_hash`, `role_id`, `status`)
VALUES ('super_admin', '10000000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 1);


-- ============================================================
-- 2. 角色表
-- ============================================================
CREATE TABLE `sys_role` (
    `id`          BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code`        VARCHAR(64)      NOT NULL                COMMENT '角色编码（super_admin / operator / content_reviewer）',
    `name`        VARCHAR(64)      NOT NULL                COMMENT '角色名称',
    `description` TEXT                     DEFAULT NULL   COMMENT '角色描述',
    `permissions` JSON                     DEFAULT NULL   COMMENT '权限码集合 JSON 数组',
    `created_at`  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 预置三个角色
INSERT INTO `sys_role` (`code`, `name`, `description`, `permissions`) VALUES
('super_admin',  '超级管理员', '全部权限',                            NULL),
('content_reviewer', '内容审核', '查看全部内容 + 题库审核；无增删改导入导出权限', '["question:view","question:review"]'),
('operator',     '运营人员',   '除系统管理、用户管理外的全部操作；不含题库审核权限', '["subject:view","subject:add","subject:edit","subject:delete",
                                                                                         "course:view","course:add","course:edit","course:delete",
                                                                                         "question:view","question:add","question:edit","question:delete",
                                                                                         "question:import","question:export",
                                                                                         "interview_question:view","interview_question:edit","interview_question:delete",
                                                                                         "route:view","route:add","route:edit","route:delete",
                                                                                         "student:view","student:edit","student:delete",
                                                                                         "dashboard:view"]');


-- ============================================================
-- 3. 动态菜单表
-- ============================================================
CREATE TABLE `sys_menu` (
    `id`              BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`       BIGINT UNSIGNED  NOT NULL DEFAULT 0      COMMENT '父菜单ID，0表示顶级菜单',
    `name`            VARCHAR(64)      NOT NULL                COMMENT '菜单名称',
    `path`            VARCHAR(256)             DEFAULT NULL   COMMENT '路由路径',
    `component`       VARCHAR(256)             DEFAULT NULL   COMMENT '前端组件路径',
    `permission_code` VARCHAR(128)             DEFAULT NULL   COMMENT '权限码（按钮级控制）',
    `sort_order`      INT              NOT NULL DEFAULT 0      COMMENT '排序号，越小越靠前',
    `menu_type`       TINYINT          NOT NULL DEFAULT 2      COMMENT '菜单类型：1=目录 2=菜单 3=按钮',
    `visible`         TINYINT          NOT NULL DEFAULT 1      COMMENT '是否可见：0=隐藏 1=显示',
    `status`          TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `created_at`      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_sort` (`parent_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- 预置菜单（4组11项）
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `permission_code`, `sort_order`, `menu_type`) VALUES
-- 概览组
(0, '数据看板',   '/dashboard',           'admin/index',   'dashboard:view',              10, 2),
-- 内容管理组
(0, '内容管理',   NULL,                   NULL,            NULL,                          20, 1),
(2, '学科管理',   '/subjects',            'admin/subjects','subject:view',                21, 2),
(2, 'B站课程库',  '/courses',             'admin/courses', 'course:view',                 22, 2),
(2, '题库管理',   '/questions',           'admin/questions','question:view',              23, 2),
(2, '面试题库',   '/interview-questions', 'admin/question-bank','interview_question:view', 24, 2),
(2, '学习路线',   '/routes',              'admin/routes',   'route:view',                  25, 2),
-- 学员管理组
(0, '学员管理',   NULL,                   NULL,            NULL,                          30, 1),
(3, '学员管理',   '/students',            'admin/students','student:view',                31, 2),
-- 系统管理组
(0, '系统管理',   NULL,                   NULL,            NULL,                          40, 1),
(4, '用户管理',   '/users',               'admin/users',   'user:view',                   41, 2),
(4, '角色管理',   '/roles',               'admin/roles',   'role:view',                   42, 2),
(4, '操作日志',   '/logs',                'admin/logs',    'log:view',                    43, 2),
(4, '系统设置',   '/settings',            'admin/settings','system_setting:view',         44, 2);


-- ============================================================
-- 4. 学科表
-- ============================================================
CREATE TABLE `subject` (
    `id`          BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(64)      NOT NULL                COMMENT '学科名称',
    `icon`        VARCHAR(16)      NOT NULL                COMMENT 'Emoji 图标',
    `description` TEXT             DEFAULT NULL                COMMENT '学科描述',
    `sort_order`  INT              NOT NULL DEFAULT 0      COMMENT '排序号，越小越靠前',
    `color`       VARCHAR(16)               DEFAULT NULL   COMMENT '主题颜色（十六进制）',
    `status`      TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `created_at`  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学科表';

-- 预置 8 个学科
INSERT INTO `subject` (`name`, `icon`, `description`, `sort_order`, `color`) VALUES
('AI应用与智能体开发', '🤖', '面向 AI 应用开发与智能体编排方向', 1,  '#409EFF'),
('AI大模型开发',      '🧠', '大语言模型训练、微调与部署方向',    2,  '#67C23A'),
('AI具身智能机器人开发', '🦾', '机器人感知与控制方向',            3,  '#E6A23C'),
('AI嵌入式_机器人开发', '⚙️', '嵌入式 AI 与边缘计算方向',        4,  '#F56C6C'),
('AI测试',            '🧪', 'AI 系统测试与质量保证方向',       5,  '#909399'),
('AI运维',            '🛠️', 'AI 基础设施运维方向',            6,  '#5CCKCA'),
('AI视觉设计',        '🎨', 'AI 辅助视觉设计与生成方向',      7,  '#AB7BFA'),
('AI视频与短剧创作',  '🎬', 'AI 视频生成与短剧创作方向',     8,  '#FF7D00');


-- ============================================================
-- 5. 阶段表
-- ============================================================
CREATE TABLE `catalog` (
    `id`          BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `subject_id`  BIGINT UNSIGNED  NOT NULL                COMMENT '所属学科 ID',
    `name`        VARCHAR(64)      NOT NULL                COMMENT '阶段名称',
    `icon`        VARCHAR(16)             DEFAULT NULL   COMMENT '图标 Emoji',
    `sort_order`  INT              NOT NULL DEFAULT 0      COMMENT '排序号',
    `status`      TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `created_at`  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_subject_status` (`subject_id`, `status`),
    KEY `idx_subject_sort`  (`subject_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阶段表（挂载在学科下）';


-- ============================================================
-- 6. 题库题目表
-- ============================================================
CREATE TABLE `question` (
    `id`             BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `catalog_id`     BIGINT UNSIGNED  NOT NULL                COMMENT '所属阶段 ID',
    `content`        TEXT             NOT NULL                COMMENT '题干内容',
    `type`           VARCHAR(32)      NOT NULL                COMMENT '题型：single_choice/multiple_choice/judge/essay',
    `difficulty`     VARCHAR(32)      NOT NULL                COMMENT '难度：easy/medium/hard',
    `options`        JSON                     DEFAULT NULL   COMMENT '选项列表 [{label, content, is_correct}]',
    `answer`         TEXT             NOT NULL                COMMENT '正确答案（单选/多选为选项标识如"AB"，判断为"正确"/"错误"）',
    `analysis`       TEXT                     DEFAULT NULL   COMMENT '解析说明',
    `tags`           JSON                     DEFAULT NULL   COMMENT '标签数组 ["Java", "Spring"]',
    `view_count`     INT              NOT NULL DEFAULT 0      COMMENT '浏览次数',
    `answer_count`   INT              NOT NULL DEFAULT 0      COMMENT '作答人数',
    `correct_count`  INT              NOT NULL DEFAULT 0      COMMENT '正确次数',
    `status`         VARCHAR(32)      NOT NULL DEFAULT 'draft'COMMENT '状态：draft/pending_review/published/offline/rejected',
    `review_comment` TEXT                     DEFAULT NULL   COMMENT '审核备注',
    `reviewed_by`    BIGINT UNSIGNED          DEFAULT NULL   COMMENT '审核人管理员 ID',
    `reviewed_at`    DATETIME                DEFAULT NULL   COMMENT '审核时间',
    `created_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_catalog_status`  (`catalog_id`, `status`),
    KEY `idx_status_type_difficulty` (`status`, `type`, `difficulty`),
    FULLTEXT KEY `idx_content_keyword` (`content`) WITH PARSER ngram,
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题库题目表';


-- ============================================================
-- 7. AI面试题库素材表
-- ============================================================
CREATE TABLE `interview_question` (
    `id`               BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content`          TEXT             NOT NULL                COMMENT '面试题目题干',
    `reference_answer` TEXT                     DEFAULT NULL   COMMENT '参考答案（用于 AI 评分对比）',
    `topic`            VARCHAR(128)             DEFAULT NULL   COMMENT '主题（自由输入，留空=未分类）',
    `difficulty`       VARCHAR(32)      NOT NULL DEFAULT 'medium' COMMENT '难度：easy/medium/hard',
    `tags`             JSON                     DEFAULT NULL   COMMENT '标签数组',
    `source_type`      VARCHAR(32)      NOT NULL DEFAULT 'manual' COMMENT '来源：manual/excel_import',
    `source_doc`       VARCHAR(256)             DEFAULT NULL   COMMENT '源文件名（excel_import 时有值）',
    `source_row`       INT                      DEFAULT NULL   COMMENT '源文件行号',
    `status`           TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `created_at`       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`       TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_topic_difficulty` (`topic`, `difficulty`),
    KEY `idx_source_type` (`source_type`),
    FULLTEXT KEY `idx_content_keyword` (`content`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI面试题库素材表';


-- ============================================================
-- 8. B站课程表
-- ============================================================
CREATE TABLE `course` (
    `id`                BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`             VARCHAR(256)     NOT NULL                COMMENT '课程名称',
    `bv_id`             VARCHAR(32)              DEFAULT NULL   COMMENT 'BV号（全库唯一）',
    `cover_url`         VARCHAR(512)             DEFAULT NULL   COMMENT '封面图片URL',
    `duration`          VARCHAR(8)                DEFAULT NULL   COMMENT '时长展示格式 HH:MM',
    `duration_minutes`  INT                       DEFAULT 0     COMMENT '时长分钟数（便于统计）',
    `source`            VARCHAR(32)      NOT NULL DEFAULT 'other'COMMENT '来源：bilibili/other',
    `link`              VARCHAR(512)             DEFAULT NULL   COMMENT '播放链接',
    `status`            TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `created_at`        DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`        DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bv_id` (`bv_id`),
    KEY `idx_title` (`title`(191)),
    KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='B站课程表';


-- ============================================================
-- 9. 课程-学科关联表（多对多）
-- ============================================================
CREATE TABLE `course_subject` (
    `course_id`    BIGINT UNSIGNED  NOT NULL COMMENT '课程ID',
    `subject_id`   BIGINT UNSIGNED  NOT NULL COMMENT '学科ID',
    `is_primary`   TINYINT          NOT NULL DEFAULT 0 COMMENT '是否主学科：1=主学科 0=关联学科',
    PRIMARY KEY (`course_id`, `subject_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_subject_id` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程-学科关联表';


-- ============================================================
-- 10. 学习路线表
-- ============================================================
CREATE TABLE `route` (
    `id`              BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `subject_id`      BIGINT UNSIGNED           DEFAULT NULL   COMMENT '主属学科ID（可为空）',
    `name`            VARCHAR(128)     NOT NULL                COMMENT '路线名称',
    `icon`            VARCHAR(16)              DEFAULT NULL   COMMENT '路线图标 Emoji',
    `description`     TEXT                    DEFAULT NULL   COMMENT '路线描述',
    `target_audience` VARCHAR(256)           DEFAULT NULL   COMMENT '适合人群',
    `status`          TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `sort_order`      INT              NOT NULL DEFAULT 0      COMMENT '排序号',
    `created_at`      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
    `updated_at`      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_subject_status` (`subject_id`, `status`),
    KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路线表';


-- ============================================================
-- 11. 路线-课程关联表（含顺序）
-- ============================================================
CREATE TABLE `course_route_item` (
    `route_id`  BIGINT UNSIGNED NOT NULL COMMENT '路线ID',
    `course_id` BIGINT UNSIGNED NOT NULL COMMENT '课程ID',
    `sort_order` INT            NOT NULL DEFAULT 0  COMMENT '排序号',
    PRIMARY KEY (`route_id`, `course_id`),
    KEY `idx_route_id_sort` (`route_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线-课程关联表';


-- ============================================================
-- 12. 学员账号表
-- ============================================================
CREATE TABLE `student_account` (
    `id`             BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `phone`          VARCHAR(20)      NOT NULL                COMMENT '手机号（唯一）',
    `email`          VARCHAR(128)             DEFAULT NULL   COMMENT '邮箱（唯一，可选）',
    `nickname`       VARCHAR(64)      NOT NULL DEFAULT '学员' COMMENT '昵称',
    `password_hash`  VARCHAR(255)     NOT NULL                COMMENT 'BCrypt 密文',
    `avatar_url`     VARCHAR(512)             DEFAULT NULL   COMMENT '头像URL',
    `gender`         VARCHAR(8)               DEFAULT '保密'  COMMENT '性别：男/女/保密',
    `status`         TINYINT          NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `last_login_at`  DATETIME                DEFAULT NULL   COMMENT '最后登录时间（CST）',
    `created_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '注册时间',
    `updated_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status_created` (`status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员账号表';


-- ============================================================
-- 13. 答题记录表（流水型，物理删除）
-- ============================================================
CREATE TABLE `answer_record` (
    `id`              BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`         BIGINT UNSIGNED  NOT NULL                COMMENT '学员ID',
    `question_id`     BIGINT UNSIGNED  NOT NULL                COMMENT '题目ID',
    `selected_answer` TEXT             NOT NULL                COMMENT '用户所选答案（选择题存选项标识如"AC"，简答题存文本）',
    `is_correct`      TINYINT          NOT NULL DEFAULT 0      COMMENT '是否答对：0=否 1=是（仅选择题/判断题有效）',
    `score`           DECIMAL(5,2)              DEFAULT NULL   COMMENT '得分（0-10，简答题由AI评分；选择题固定0或10）',
    `pass_status`     TINYINT          NOT NULL DEFAULT 0      COMMENT '通过状态：0=未通过 1=通过（≥6分）',
    `answered_at`     DATETIME         NOT NULL                COMMENT '作答时间（CST）',
    `created_at`      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '记录创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_question` (`user_id`, `question_id`) COMMENT '同一用户对同一题只计一次错题/答题统计',
    KEY `idx_user_answered_at` (`user_id`, `answered_at` DESC) COMMENT '学员历史答题 & 学习天数统计',
    KEY `idx_question_answered_at` (`question_id`, `answered_at` DESC) COMMENT '题目答题统计聚合',
    -- MySQL 8.0 函数索引：按日期分组统计每日练习量
    KEY `idx_answered_date` ((DATE(`answered_at`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题记录表（流水型，物理删除）';


-- ============================================================
-- 14. 错题本记录表
-- ============================================================
CREATE TABLE `wrong_question` (
    `id`             BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`        BIGINT UNSIGNED  NOT NULL                COMMENT '学员ID',
    `question_id`    BIGINT UNSIGNED  NOT NULL                COMMENT '题目ID',
    `error_count`    INT              NOT NULL DEFAULT 0      COMMENT '累计错误次数',
    `correct_count`  INT              NOT NULL DEFAULT 0      COMMENT '连续答对次数（纠正进度）',
    `last_error_at`  DATETIME                DEFAULT NULL   COMMENT '最近答错时间（CST）',
    `is_mastered`    TINYINT          NOT NULL DEFAULT 0      COMMENT '是否已掌握：0=否 1=是',
    `mastered_at`    DATETIME                DEFAULT NULL   COMMENT '掌握时间',
    `created_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '首次进入错题时间',
    `updated_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `is_removed`     TINYINT          NOT NULL DEFAULT 0      COMMENT '是否已移除（标记掌握或手动移除）：0=否 1=是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_question_unique` (`user_id`, `question_id`),
    KEY `idx_user_not_mastered` (`user_id`, `is_mastered`, `is_removed`) COMMENT '待复习错题列表查询',
    KEY `idx_question_error_count` (`question_id`, `error_count` DESC) COMMENT '高频错题排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本记录表';


-- ============================================================
-- 15. 练习会话表（物理删除）
-- ============================================================
CREATE TABLE `practice_session` (
    `id`             BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`        BIGINT UNSIGNED  NOT NULL                COMMENT '学员ID',
    `mode`           VARCHAR(32)      NOT NULL                COMMENT '练习模式：sequence/random',
    `catalog_id`     BIGINT UNSIGNED           DEFAULT NULL   COMMENT '所练阶段ID',
    `question_ids`   JSON             NOT NULL                COMMENT '题目ID数组（按实际顺序）',
    `current_index`  INT              NOT NULL DEFAULT 0      COMMENT '当前题号索引（从0开始）',
    `elapsed_seconds` INT             NOT NULL DEFAULT 0      COMMENT '本次已用时长（秒）',
    `answers`        JSON                     DEFAULT NULL   COMMENT '各题答案快照 {questionId: {selected, scored, passed}}',
    `created_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '开始练习时间',
    `updated_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_updated` (`user_id`, `updated_at` DESC) COMMENT '继续上次练习查找'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习会话表（物理删除）';


-- ============================================================
-- 16. AI面试记录表
-- ============================================================
CREATE TABLE `interview_record` (
    `id`             BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`        BIGINT UNSIGNED  NOT NULL                COMMENT '学员ID',
    `direction`      VARCHAR(64)      NOT NULL                COMMENT '技术方向',
    `difficulty`     VARCHAR(32)      NOT NULL DEFAULT 'medium' COMMENT '面试难度：easy/medium/hard',
    `question_count` INT              NOT NULL                COMMENT '面试题数量',
    `total_score`    DECIMAL(5,2)             DEFAULT NULL   COMMENT '综合评分（0-10）',
    `duration_sec`   INT                       DEFAULT 0     COMMENT '用时（秒）',
    `started_at`     DATETIME         NOT NULL                COMMENT '面试开始时间（CST）',
    `completed_at`   DATETIME                DEFAULT NULL   COMMENT '面试结束时间（CST）',
    `created_at`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '记录创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_started` (`user_id`, `started_at` DESC) COMMENT '面试历史记录分页查询',
    KEY `idx_user_direction` (`user_id`, `direction`)        COMMENT '同方向成绩趋势对比'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI面试记录表';


-- ============================================================
-- 17. 面试对话消息表（物理删除）
-- ============================================================
CREATE TABLE `interview_message` (
    `id`                   BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `interview_record_id`  BIGINT UNSIGNED  NOT NULL                COMMENT '面试记录ID',
    `role`                 VARCHAR(16)      NOT NULL                COMMENT '角色：ai/user',
    `content`              TEXT             NOT NULL                COMMENT '对话内容',
    `score`                DECIMAL(5,2)             DEFAULT NULL   COMMENT '该题得分（AI给出，null=尚未评分）',
    `created_at`           DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '消息时间',
    PRIMARY KEY (`id`),
    KEY `idx_interview_record` (`interview_record_id`, `created_at`) COMMENT '面试回放消息序列'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试对话消息表（物理删除）';


-- ============================================================
-- 18. 学员路线学习进度表
-- ============================================================
CREATE TABLE `student_route_progress` (
    `id`               BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`          BIGINT UNSIGNED  NOT NULL                COMMENT '学员ID',
    `route_id`         BIGINT UNSIGNED  NOT NULL                COMMENT '路线ID',
    `completed_count`  INT              NOT NULL DEFAULT 0      COMMENT '已完成课程数',
    `total_count`      INT              NOT NULL DEFAULT 0      COMMENT '路线总课程数',
    `last_study_at`    DATETIME                DEFAULT NULL   COMMENT '最近学习时间（CST）',
    `created_at`       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '首次学习路线时间',
    `updated_at`       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `is_deleted`       TINYINT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常 1=已清除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_route_unique` (`user_id`, `route_id`),
    KEY `idx_user_last_study` (`user_id`, `last_study_at` DESC) COMMENT '最近学习的路线排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员路线学习进度表';


-- ============================================================
-- 19. 每日练习量统计表（定时任务刷新，减少首页查询压力）
-- ============================================================
CREATE TABLE `daily_practice_stat` (
    `id`               BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stat_date`        DATE             NOT NULL                COMMENT '统计日期（CST 日界）',
    `user_id`          BIGINT UNSIGNED  NOT NULL                COMMENT '学员ID，NULL=全站汇总',
    `practice_count`   INT              NOT NULL DEFAULT 0      COMMENT '当日答题总数',
    `correct_count`    INT              NOT NULL DEFAULT 0      COMMENT '当日正确数',
    `accuracy_rate`    DECIMAL(5,2)             DEFAULT 0.00    COMMENT '当日正确率（数值型，如73.50表示73.5%，范围0~100）',
    `interview_count`  INT              NOT NULL DEFAULT 0      COMMENT '当日面试次数',
    `avg_interview_score` DECIMAL(5,2)          DEFAULT NULL   COMMENT '当日面试平均分数',
    `created_at`       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '记录创建时间',
    `updated_at`       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_date_user` (`stat_date`, `user_id`),
    KEY `idx_stat_date` (`stat_date` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日练习量统计表（定时任务刷新）';

-- 全站汇总占位记录（user_id=0）
INSERT INTO `daily_practice_stat` (`stat_date`, `user_id`, `practice_count`, `correct_count`, `interview_count`)
SELECT CURDATE(), 0, COUNT(*), SUM(is_correct), 0 FROM `answer_record`
WHERE DATE(`answered_at`) = CURDATE();


-- ============================================================
-- 20. 系统设置表
-- ============================================================
CREATE TABLE `sys_setting` (
    `id`           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`   VARCHAR(128)     NOT NULL                COMMENT '配置键',
    `config_value` TEXT             NOT NULL                COMMENT '配置值',
    `description`  VARCHAR(256)             DEFAULT NULL   COMMENT '配置说明',
    `updated_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统基本设置表';

-- 默认设置
INSERT INTO `sys_setting` (`config_key`, `config_value`, `description`) VALUES
('platform.name',     '云枢智学',   '平台名称，显示在浏览器标题和Logo旁'),
('platform.description', 'AI赋能的智能面试训练平台', '平台副标题描述'),
('platform.logo_url', '',           '平台Logo图片URL（建议256×256）'),
('platform.default_avatar_url', '', '用户未设置头像时的默认头像URL');


-- ============================================================
-- 21. 操作日志表
-- ============================================================
CREATE TABLE `operation_log` (
    `id`           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      BIGINT UNSIGNED  NOT NULL                COMMENT '操作用户ID',
    `username`     VARCHAR(64)      NOT NULL DEFAULT ''     COMMENT '操作用户名（冗余，便于查询）',
    `action_type`  VARCHAR(64)      NOT NULL                COMMENT '操作类型：login/create/update/delete/disable/enable/review等',
    `target_type`  VARCHAR(64)               DEFAULT NULL   COMMENT '操作对象类型：question/course/route等',
    `target_id`    BIGINT UNSIGNED           DEFAULT NULL   COMMENT '操作对象ID',
    `detail`       TEXT                     DEFAULT NULL   COMMENT '操作详情（JSON格式）',
    `ip_address`   VARCHAR(64)               DEFAULT NULL   COMMENT '客户端IP地址',
    `created_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '操作时间（CST）',
    PRIMARY KEY (`id`),
    KEY `idx_user_created`     (`user_id`, `created_at` DESC),
    KEY `idx_action_created`   (`action_type`, `created_at` DESC),
    KEY `idx_target_type`      (`target_type`),
    KEY `idx_target`           (`target_type`, `target_id`),
    KEY `idx_created_at`       (`created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';


-- ============================================================
-- 22. 短信验证码记录表（临时数据，定期清理）
-- ============================================================
CREATE TABLE `sms_verification_code` (
    `id`           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `phone`        VARCHAR(20)      NOT NULL                COMMENT '手机号',
    `code`         VARCHAR(10)      NOT NULL                COMMENT '验证码（明文，仅供比对）',
    `purpose`      VARCHAR(64)      NOT NULL                COMMENT '用途：register/login/forgot_password',
    `fail_count`   INT              NOT NULL DEFAULT 0      COMMENT '连续校验失败次数',
    `expired_at`   DATETIME         NOT NULL                COMMENT '过期时间（创建后60分钟）',
    `created_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '发送时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone_purpose_created` (`phone`, `purpose`, DATE_FORMAT(`created_at`, '%Y-%m-%d %H'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码临时记录表（定时任务清理过期数据）';
