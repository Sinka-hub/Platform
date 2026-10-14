package com.sinka.platform.task;

import com.sinka.platform.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 60_000)
    public void closeExpiredOrders() {
        int rows = orderService.closeExpiredUnpaidOrders();

        if (rows > 0) {
            log.info("定时兜底关闭超时未支付订单，数量：{}", rows);
        }
    }
}