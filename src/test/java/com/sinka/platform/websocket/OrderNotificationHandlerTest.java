package com.sinka.platform.websocket;

import cn.hutool.jwt.JWT;
import com.sinka.platform.constant.JwtConstant;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.*;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderNotificationHandlerTest {
    private WebSocketSession session(String id) {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn(id);
        when(session.getAttributes()).thenReturn(new ConcurrentHashMap<>());
        when(session.isOpen()).thenReturn(true);
        return session;
    }

    private String token(Long userId, Instant expires) {
        return JWT.create().setKey(JwtConstant.SECRET_KEY.getBytes())
                .setPayload(JwtConstant.User_ID, userId.toString())
                .setExpiresAt(Date.from(expires)).sign();
    }

    private void authenticate(OrderNotificationHandler handler, WebSocketSession session, Long userId) throws Exception {
        handler.afterConnectionEstablished(session);
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"AUTH\",\"token\":\""
                + token(userId, Instant.now().plusSeconds(60)) + "\"}"));
    }

    @Test
    void sendsOnlyToAuthenticatedBuyerIncludingMultipleTabs() throws Exception {
        var handler = new OrderNotificationHandler();
        var buyer = session("buyer");
        var secondTab = session("tab");
        var other = session("other");
        var anonymous = session("anonymous");
        authenticate(handler, buyer, 26L);
        authenticate(handler, secondTab, 26L);
        authenticate(handler, other, 3L);
        handler.afterConnectionEstablished(anonymous);
        clearInvocations(buyer, secondTab, other, anonymous);
        handler.remind(26L, 20L, "2030-01-01T00:00:00+08:00");
        verify(buyer).sendMessage(argThat(message -> message.getPayload().toString().contains("ORDER_PAYMENT_REMINDER")));
        verify(secondTab).sendMessage(any(TextMessage.class));
        verify(other, never()).sendMessage(any());
        verify(anonymous, never()).sendMessage(any());
    }

    @Test
    void rejectsInvalidAndExpiredTokens() throws Exception {
        var handler = new OrderNotificationHandler();
        var invalid = session("invalid");
        handler.afterConnectionEstablished(invalid);
        handler.handleTextMessage(invalid, new TextMessage("{\"type\":\"AUTH\",\"token\":\"invalid\"}"));
        verify(invalid).close(CloseStatus.POLICY_VIOLATION);
        var expired = session("expired");
        handler.afterConnectionEstablished(expired);
        handler.handleTextMessage(expired, new TextMessage("{\"type\":\"AUTH\",\"token\":\""
                + token(26L, Instant.now().minusSeconds(10)) + "\"}"));
        verify(expired).close(CloseStatus.POLICY_VIOLATION);
    }

    @Test
    void disconnectedSessionsNoLongerReceiveReminders() throws Exception {
        var handler = new OrderNotificationHandler();
        var buyer = session("buyer");
        authenticate(handler, buyer, 26L);
        handler.afterConnectionClosed(buyer, CloseStatus.NORMAL);
        clearInvocations(buyer);
        handler.remind(26L, 20L, "2030-01-01T00:00:00+08:00");
        verify(buyer, never()).sendMessage(any());
    }
}
