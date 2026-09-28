package com.sinka.platform.utils;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class RedisIdWorker {

    public static final long START_TIMESTAMP = 1767225600L;//2026.1.1.0.0.0

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    public long nextId(String prefix) {
        //获取当前时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowTimestamp = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowTimestamp - START_TIMESTAMP;
        //生成序列号
        //获取当天日期
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = redissonClient.getAtomicLong("icr:"+prefix+":"+date).incrementAndGet();

        return timestamp << 32 | count;
    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        long epochSecond = localDateTime.toEpochSecond(ZoneOffset.UTC);
        System.out.println(epochSecond);

    }
}
