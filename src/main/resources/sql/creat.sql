CREATE DATABASE IF NOT EXISTS platformdb;
USE platformdb;

create table orders
(
    id                     bigint unsigned auto_increment comment '订单ID'
        primary key,
    order_no               varchar(32)                           not null comment '订单编号',
    product_id             bigint unsigned                       not null comment '商品ID，对应products.id',
    orderer_id             bigint unsigned                       not null comment '下单用户ID，对应users.id',
    publisher_id           bigint unsigned                       not null comment '下单时的商品发布用户ID，对应users.id',
    product_title_snapshot varchar(100)                          not null comment '下单时的商品标题快照',
    unit_price_snapshot    decimal(10, 2)                        not null comment '下单时的成交价格快照',
    order_status           varchar(30) default 'WAITING_PAYMENT' not null comment '订单状态：WAITING_PAYMENT待支付，WAITING_ACCEPTANCE待接取，IN_PROGRESS进行中，WAITING_CONFIRMATION待验收，COMPLETED已完成',
    payment_status         varchar(20) default 'UNPAID'          not null comment '支付状态：UNPAID未支付，PAID已支付，REFUNDED已退款',
    created_at             datetime    default CURRENT_TIMESTAMP not null comment '下单时间',
    paid_at                datetime                              null comment '支付时间',
    accepted_at            datetime                              null comment '发布者接取时间',
    submitted_at           datetime                              null comment '发布者提交完成时间',
    confirmed_at           datetime                              null comment '下单用户验收时间',
    updated_at             datetime    default CURRENT_TIMESTAMP not null comment '最后更新时间',
    quantity               int         default 1                 not null comment '商品数量',
    total_amount           decimal(10, 2)                        not null comment '商品购买总价=unit_price_snapshot*quantity',
    constraint uk_orders_order_no
        unique (order_no)
)
    comment '订单表';

create table products
(
    id             bigint unsigned auto_increment comment '商品ID'
        primary key,
    publisher_id   bigint unsigned                       not null comment '发布用户ID，对应users.id',
    product_type   varchar(30) default 'SERVICE'         not null comment '商品类型：SERVICE服务商品，预留后续扩展',
    title          varchar(100)                          not null comment '商品标题',
    description    text                                  not null comment '商品描述',
    price          decimal(10, 2)                        not null comment '商品价格或服务金额',
    product_status varchar(20) default 'DRAFT'           not null comment '发布状态：DRAFT草稿，PUBLISHED已发布，OFF_SHELF已下架',
    created_at     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at     datetime    default CURRENT_TIMESTAMP not null comment '最后更新时间'
)
    comment '商品表';

create table users
(
    id             bigint unsigned auto_increment comment '用户ID'
        primary key,
    username       varchar(50)                           not null comment '登录账号',
    password       varchar(100)                          not null comment '密码（练习阶段临时明文，正式使用前必须改为BCrypt哈希）',
    nickname       varchar(50)                           not null comment '用户昵称',
    avatar_url     varchar(500)                          null comment '头像地址',
    account_status varchar(20) default 'NORMAL'          not null comment '账号状态：NORMAL正常，DISABLED禁用',
    created_at     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at     datetime    default CURRENT_TIMESTAMP not null comment '最后更新时间',
    constraint uk_users_username
        unique (username)
)
    comment '用户表';
CREATE TABLE coin_wallets (
                              id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
                              user_id    BIGINT UNSIGNED NOT NULL COMMENT '用户ID，对应users.id',
                              balance    DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT '可用金币余额',
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                              PRIMARY KEY (id),
                              UNIQUE KEY uk_coin_wallets_user_id (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户金币钱包';


CREATE TABLE coin_transactions (
                                   id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
                                   user_id       BIGINT UNSIGNED NOT NULL COMMENT '余额发生变动的用户ID',
                                   order_id      BIGINT UNSIGNED NULL COMMENT '关联订单ID，充值时为空',
                                   type          VARCHAR(30) NOT NULL COMMENT '流水类型：RECHARGE充值、ORDER_PAY支付、ORDER_REFUND退款、ORDER_SETTLE结算',
                                   change_amount DECIMAL(18, 2) NOT NULL COMMENT '金币变动金额，收入为正，支出为负',
                                   biz_no        VARCHAR(100) NOT NULL COMMENT '业务编号，用于防止重复记账',
                                   created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                   PRIMARY KEY (id),
                                   UNIQUE KEY uk_coin_transactions_biz_no (biz_no),
                                   KEY idx_coin_transactions_user_id_id (user_id, id),
                                   KEY idx_coin_transactions_order_id (order_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '金币变动流水';