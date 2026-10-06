package com.sinka.platform.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitPublisherConfig {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void registerReturnCallback() {
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error(
                    "消息无法路由，交换机：{}，路由键：{}，原因：{}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText()
            );
        });
    }
}