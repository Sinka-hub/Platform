package com.sinka.platform.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetailVO {
    private Long id;
    private Long publisherId;
    private String productType;
    private String title;
    private String description;
    private BigDecimal price;
}