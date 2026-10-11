package com.sinka.platform.consumer;

import com.sinka.platform.entity.Order;
import com.sinka.platform.mapper.OrderMapper;
import com.sinka.platform.websocket.OrderNotificationHandler;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class OrderRemindConsumerTest {
    @Test
    void doesNotNotifyWhenDatabaseFindsNoEligibleOrder() {
        var mapper = mock(OrderMapper.class);
        var handler = mock(OrderNotificationHandler.class);
        new OrderRemindConsumer(mapper, handler).consume("20");
        verify(mapper).selectUnpaidOrderForReminder(20L);
        verifyNoInteractions(handler);
    }

    @Test
    void notifiesBuyerWithExplicitBeijingDeadline() {
        var mapper = mock(OrderMapper.class);
        var handler = mock(OrderNotificationHandler.class);
        var order = new Order();
        order.setId(20L);
        order.setOrdererId(26L);
        order.setCreatedAt(LocalDateTime.of(2026, 10, 11, 15, 0));
        when(mapper.selectUnpaidOrderForReminder(20L)).thenReturn(order);
        new OrderRemindConsumer(mapper, handler).consume("20");
        verify(handler).remind(26L, 20L, "2026-10-11T15:15+08:00");
    }
}
