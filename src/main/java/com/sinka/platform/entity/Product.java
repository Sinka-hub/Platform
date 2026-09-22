package com.sinka.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {

    private Long id;

    private Long publisherId;

    private String productType;

    private String title;

    private String description;

    private BigDecimal price;

    private String productStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}