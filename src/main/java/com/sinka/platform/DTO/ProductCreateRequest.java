package com.sinka.platform.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "发布商品请求")
public class ProductCreateRequest {

    @Schema(description = "商品类型", example = "SERVICE")
    @NotBlank(message = "商品类型不能为空")
    @Size(max = 30, message = "商品类型不能超过30个字符")
    private String productType;

    @Schema(description = "商品标题", example = "示例商品")
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题不能超过100个字符")
    private String title;

    @Schema(description = "商品描述", example = "提供一次游戏陪练服务")
    @NotBlank(message = "商品描述不能为空")
    private String description;

    @Schema(description = "商品价格，单位：元", example = "12.50")
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格最多8位整数、2位小数")
    private BigDecimal price;
}
