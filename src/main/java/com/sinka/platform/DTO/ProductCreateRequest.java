package com.sinka.platform.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "商品类型不能为空")
    @Size(max = 30, message = "商品类型不能超过30个字符")
    private String productType;

    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题不能超过100个字符")
    private String title;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格最多8位整数、2位小数")
    private BigDecimal price;
}