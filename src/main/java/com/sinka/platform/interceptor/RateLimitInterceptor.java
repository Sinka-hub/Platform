package com.sinka.platform.interceptor;

import com.sinka.platform.utils.ThreadLocalIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public RateLimitInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT =
            new DefaultRedisScript<>("""
                local count = redis.call('INCR', KEYS[1])
                if count == 1 then
                    redis.call('EXPIRE', KEYS[1], ARGV[1])
                end
                return count
                """, Long.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String path = request.getServletPath();
        String method = request.getMethod();

        // 非 POST 请求不限制
        if (!"POST".equals(method)) {
            return true;
        }

        String keyPrefix;

        if ("/orders".equals(path)) {
            keyPrefix = "rate:order:create:";
        } else if ("/wallet/recharge".equals(path)) {
            keyPrefix = "rate:wallet:recharge:";
        } else {
            return true;
        }

        Long userId = ThreadLocalIdUtil.getUserId();
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String key = keyPrefix + userId;

        Long count;
        try {
            count = stringRedisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    Collections.singletonList(key),
                    "10"
            );
        } catch (DataAccessException e) {
            log.warn("Redis 限流不可用，跳过下单限流，用户ID：{}", userId, e);
            return true;
        }

        if (count == null) {
            log.warn("Redis 限流未返回计数，跳过下单限流，用户ID：{}", userId);
            return true;
        }

        if (count > 3) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"code\":0,\"msg\":\"操作过于频繁，请稍后重试\",\"data\":null}"
            );
            return false;
        }

        return true;

    }
}