package com.sinka.platform.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建订单请求")
public class OrderCreateRequest {
    @Schema(description = "商品 ID", example = "1")
    @NotNull
    private Long productId;

    @Schema(description = "购买数量", example = "2")
    @NotNull
    @Min(1)
    private Integer quantity;
}
