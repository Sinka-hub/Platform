package com.sinka.platform.VO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductListVO {
    private Long id;
    private Long publisherId;
    private String title;
    private String description;
    private BigDecimal price;
}