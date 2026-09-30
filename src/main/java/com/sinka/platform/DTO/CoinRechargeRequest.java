package com.sinka.platform.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoinRechargeRequest {

    private BigDecimal amount;

    private String requestId;
}