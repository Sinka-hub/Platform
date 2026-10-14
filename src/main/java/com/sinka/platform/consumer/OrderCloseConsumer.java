package com.sinka.platform.consumer;

import com.sinka.platform.constant.RabbitConstants;
import com.sinka.platform.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCloseConsumer {

    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = RabbitConstants.ORDER_CLOSE_QUEUE,
                    durable = "true"
            ),
            exchange = @Exchange(
                    value = RabbitConstants.ORDER_CLOSE_EXCHANGE,
                    type = ExchangeTypes.DIRECT,
                    durable = "true"
            ),
            key = RabbitConstants.ORDER_CLOSE_ROUTING_KEY
    ))
    public void consume(String orderId) {
        //throw new IllegalStateException("测试消费失败");
        orderService.closeTimeoutOrder(Long.valueOf(orderId));
    }
}