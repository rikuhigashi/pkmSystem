CREATE TABLE flyway_schema_history
(
    installed_rank INT                     NOT NULL,
    version        VARCHAR(50)             NULL,
    `description`  VARCHAR(200)            NOT NULL,
    type           VARCHAR(20)             NOT NULL,
    script         VARCHAR(1000)           NOT NULL,
    checksum       INT                     NULL,
    installed_by   VARCHAR(100)            NOT NULL,
    installed_on   timestamp DEFAULT NOW() NOT NULL,
    execution_time INT                     NOT NULL,
    success        TINYINT(1)              NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (installed_rank)
);

CREATE TABLE maindata
(
    id           INT AUTO_INCREMENT      NOT NULL,
    side_item_id INT                     NULL,
    tiptap_json  JSON                    NULL,
    created_at   timestamp DEFAULT NOW() NULL COMMENT '创建时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE sidedata
(
    id         INT AUTO_INCREMENT       NOT NULL,
    icon       VARCHAR(255)             NULL,
    current    TINYINT(1) DEFAULT 0     NULL,
    href       VARCHAR(255)             NULL,
    name       VARCHAR(255)             NULL COMMENT '显示名称',
    created_at timestamp  DEFAULT NOW() NULL COMMENT '创建时间',
    updated_at timestamp  DEFAULT NOW() NULL COMMENT '更新时间',
    user_id    INT                      NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE user
(
    id            INT AUTO_INCREMENT      NOT NULL,
    account       VARCHAR(100)            NOT NULL COMMENT '用户账号',
    password_hash VARCHAR(60)             NOT NULL COMMENT '用户密码',
    username      VARCHAR(100)            NULL COMMENT '用户名字',
    roleid        VARCHAR(100)            NULL COMMENT '用户角色 0 管理员 1 用户',
    email         VARCHAR(255)            NULL COMMENT '邮箱',
    created_at    timestamp DEFAULT NOW() NULL COMMENT '创建时间',
    updated_at    timestamp DEFAULT NOW() NULL COMMENT '更新时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE INDEX flyway_schema_history_s_idx ON flyway_schema_history (success);

ALTER TABLE maindata
    ADD CONSTRAINT maindata_ibfk_1 FOREIGN KEY (side_item_id) REFERENCES sidedata (id) ON DELETE NO ACTION;

CREATE INDEX side_item_id ON maindata (side_item_id);

ALTER TABLE sidedata
    ADD CONSTRAINT sidedata_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE NO ACTION;

CREATE INDEX user_id ON sidedata (user_id);