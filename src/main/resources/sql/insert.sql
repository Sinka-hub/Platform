
-- 先添加两个用户：商品发布者和下单用户
INSERT INTO `users` (
    `id`,
    `username`,
    `password`,
    `nickname`,
    `account_status`
) VALUES
      (1, 'publisher_demo', 'demo123456', '示例发布者', 'NORMAL'),
      (2, 'orderer_demo', 'demo123456', '示例下单用户', 'NORMAL');


-- 再添加发布者的服务商品
INSERT INTO `products` (
    `id`,
    `publisher_id`,
    `product_type`,
    `title`,
    `description`,
    `price`,
    `product_status`
) VALUES (
             1,
             1,
             'SERVICE',
             '示例跑刀服务',
             '这是一条用于测试的服务商品。',
             88.00,
             'PUBLISHED'
         );


-- 最后添加一个关联该商品的待支付订单
INSERT INTO `orders` (
    `id`,
    `order_no`,
    `product_id`,
    `orderer_id`,
    `publisher_id`,
    `product_title_snapshot`,
    `unit_price_snapshot`,
    `order_status`,
    `payment_status`,
    `total_amount`
) VALUES (
             1,
             'TEST202609220001',
             1,
             2,
             1,
             '示例跑刀服务',
             88.00,
             'WAITING_PAYMENT',
             'UNPAID',
             '88.00'
         );