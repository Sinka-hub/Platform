package com.sinka.platform.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "创建订单结果")
public class OrderCreateVO {
    @Schema(description = "订单 ID，后续查询和操作订单使用")
    private Long id;
    @Schema(description = "订单编号")
    private String orderNo;
}
