package com.sinka.platform.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CoinTransactionVO {

    private Long id;
    private Long orderId;
    private String type;
    private BigDecimal changeAmount;
    private LocalDateTime createdAt;
}