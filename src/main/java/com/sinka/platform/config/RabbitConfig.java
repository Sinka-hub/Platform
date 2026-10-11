package com.sinka.platform.config;

import com.sinka.platform.constant.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue orderRemindDelayQueue() {
        return QueueBuilder.durable(RabbitConstants.ORDER_REMIND_DELAY_QUEUE)
                .ttl(RabbitConstants.ORDER_REMIND_MILLIS)
                .deadLetterExchange(RabbitConstants.ORDER_REMIND_EXCHANGE)
                .deadLetterRoutingKey(RabbitConstants.ORDER_REMIND_KEY).build();
    }

    @Bean
    public Binding orderRemindDelayBinding() {
        return BindingBuilder.bind(orderRemindDelayQueue()).to(orderDelayExchange())
                .with(RabbitConstants.ORDER_REMIND_DELAY_KEY);
    }


    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(
                RabbitConstants.ORDER_DELAY_EXCHANGE, true, false
        );
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder
                .durable(RabbitConstants.ORDER_DELAY_QUEUE)
                .ttl(RabbitConstants.ORDER_TIMEOUT_MILLIS)
                .deadLetterExchange(RabbitConstants.ORDER_CLOSE_EXCHANGE)
                .deadLetterRoutingKey(RabbitConstants.ORDER_CLOSE_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder
                .bind(orderDelayQueue())
                .to(orderDelayExchange())
                .with(RabbitConstants.ORDER_DELAY_ROUTING_KEY);
    }
    @Bean
    public DirectExchange orderErrorExchange() {
        return new DirectExchange(
                RabbitConstants.ORDER_ERROR_EXCHANGE, true, false
        );
    }

    @Bean
    public Queue orderErrorQueue() {
        return QueueBuilder
                .durable(RabbitConstants.ORDER_ERROR_QUEUE)
                .build();
    }

    @Bean
    public Binding orderErrorBinding() {
        return BindingBuilder
                .bind(orderErrorQueue())
                .to(orderErrorExchange())
                .with(RabbitConstants.ORDER_ERROR_ROUTING_KEY);
    }
    //消息重试策略
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(
                rabbitTemplate,
                RabbitConstants.ORDER_ERROR_EXCHANGE,
                RabbitConstants.ORDER_ERROR_ROUTING_KEY
        );
    }
}
