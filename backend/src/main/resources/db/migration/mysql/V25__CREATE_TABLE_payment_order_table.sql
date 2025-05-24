CREATE TABLE payment_order
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    order_no       VARCHAR(255)          NULL,
    amount         DECIMAL               NULL,
    subject        VARCHAR(255)          NULL,
    body           VARCHAR(255)          NULL,
    pay_type       VARCHAR(255)          NULL,
    status         VARCHAR(255)          NULL,
    user_id        INT                   NULL,
    create_time    datetime              NULL,
    expire_time    datetime              NULL,
    transaction_id VARCHAR(255)          NULL,
    CONSTRAINT pk_payment_order PRIMARY KEY (id)
);

ALTER TABLE payment_order
    ADD CONSTRAINT uc_payment_order_orderno UNIQUE (order_no);

ALTER TABLE payment_order
    ADD CONSTRAINT FK_PAYMENT_ORDER_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);