package com.sinka.platform.consumer;

import com.sinka.platform.constant.RabbitConstants;
import com.sinka.platform.mapper.OrderMapper;
import com.sinka.platform.websocket.OrderNotificationHandler;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class OrderRemindConsumer {
    private final OrderMapper orderMapper;
    private final OrderNotificationHandler notifications;

    public OrderRemindConsumer(OrderMapper orderMapper, OrderNotificationHandler notifications) {
        this.orderMapper = orderMapper;
        this.notifications = notifications;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstants.ORDER_REMIND_QUEUE, durable = "true"),
            exchange = @Exchange(value = RabbitConstants.ORDER_REMIND_EXCHANGE, durable = "true"),
            key = RabbitConstants.ORDER_REMIND_KEY))
    public void consume(String orderId) {
        var order = orderMapper.selectUnpaidOrderForReminder(Long.valueOf(orderId));
        if (order == null) return;
        notifications.remind(order.getOrdererId(), order.getId(),
                order.getCreatedAt().plusMinutes(15).atZone(ZoneId.of("Asia/Shanghai"))
                        .toOffsetDateTime().toString());
    }
}
