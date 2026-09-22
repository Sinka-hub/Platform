package com.sinka.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {

    private Long id;

    private String orderNo;

    private Long productId;

    private Long ordererId;

    private Long publisherId;

    private String productTitleSnapshot;

    private BigDecimal unitPriceSnapshot;

    private String orderStatus;

    private String paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime confirmedAt;

    private LocalDateTime updatedAt;

    private Integer quantity;

    private BigDecimal totalAmount;
}