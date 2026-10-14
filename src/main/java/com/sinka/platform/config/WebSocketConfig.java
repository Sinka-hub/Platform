package com.sinka.platform.config;

import com.sinka.platform.websocket.OrderNotificationHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final OrderNotificationHandler handler;

    public WebSocketConfig(OrderNotificationHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 保持默认同源限制，开发和部署都通过前端代理访问。
        registry.addHandler(handler, "/ws");
    }
}
