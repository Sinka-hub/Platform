package com.sinka.platform.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "商品列表项")
public class ProductListVO {
    @Schema(description = "商品 ID")
    private Long id;
    @Schema(description = "发布者用户 ID")
    private Long publisherId;
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品描述")
    private String description;
    @Schema(description = "商品价格，单位：元")
    private BigDecimal price;
}
