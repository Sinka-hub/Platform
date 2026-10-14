package com.sinka.platform.websocket;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.sinka.platform.constant.JwtConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OrderNotificationHandler extends TextWebSocketHandler {
    private record Connection(WebSocketSession session, long openedAt) {}
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.setTextMessageSizeLimit(8192);
        connections.put(session.getId(), new Connection(
                new ConcurrentWebSocketSessionDecorator(session, 5000, 65536),
                System.currentTimeMillis()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Connection connection = connections.get(session.getId());
        if (connection == null) return;
        try {
            var body = JSONUtil.parseObj(message.getPayload());
            if (!session.getAttributes().containsKey("jwt")) {
                if (!"AUTH".equals(body.getStr("type"))) throw new IllegalArgumentException();
                JWT jwt = JWT.of(body.getStr("token"));
                if (!jwt.setKey(JwtConstant.SECRET_KEY.getBytes()).validate(0)
                        || jwt.getPayload("exp") == null) throw new IllegalArgumentException();
                Long userId = Long.valueOf(jwt.getPayload(JwtConstant.User_ID).toString());
                session.getAttributes().put("jwt", jwt);
                session.getAttributes().put("userId", userId);
                connection.session().sendMessage(new TextMessage("{\"type\":\"READY\"}"));
            } else if (!valid(session)) {
                close(session, CloseStatus.POLICY_VIOLATION);
            } else if ("PING".equals(body.getStr("type"))) {
                connection.session().sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
            }
        } catch (RuntimeException e) {
            close(session, CloseStatus.POLICY_VIOLATION);
        }
    }

    private boolean valid(WebSocketSession session) {
        Object jwt = session.getAttributes().get("jwt");
        return jwt instanceof JWT token && token.validate(0);
    }

    public void remind(Long userId, Long orderId, String expiresAt) {
        String payload = JSONUtil.toJsonStr(Map.of("type", "ORDER_PAYMENT_REMINDER",
                "orderId", orderId, "expiresAt", expiresAt));
        for (Connection connection : connections.values()) {
            WebSocketSession session = connection.session();
            if (!userId.equals(session.getAttributes().get("userId"))) continue;
            try {
                if (!valid(session)) close(session, CloseStatus.POLICY_VIOLATION);
                else if (session.isOpen()) session.sendMessage(new TextMessage(payload));
            } catch (IOException | RuntimeException e) {
                log.warn("订单提醒连接发送失败，订单ID：{}", orderId);
                close(session, CloseStatus.SERVER_ERROR);
            }
        }
    }

    @Scheduled(fixedDelay = 10_000)
    public void cleanConnections() {
        for (Connection connection : connections.values()) {
            WebSocketSession session = connection.session();
            if (!session.isOpen() || (session.getAttributes().containsKey("jwt")
                    ? !valid(session) : System.currentTimeMillis() - connection.openedAt() >= 10_000)) {
                close(session, CloseStatus.POLICY_VIOLATION);
            }
        }
    }

    private void close(WebSocketSession session, CloseStatus status) {
        connections.remove(session.getId());
        try { session.close(status); } catch (IOException ignored) { }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        connections.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        close(session, CloseStatus.SERVER_ERROR);
    }
}
