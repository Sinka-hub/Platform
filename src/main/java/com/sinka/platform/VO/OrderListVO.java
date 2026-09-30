package com.sinka.platform.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单列表项")
public class OrderListVO {
    @Schema(description = "订单 ID")
    private Long id;
    @Schema(description = "订单编号")
    private String orderNo;
    @Schema(description = "商品 ID")
    private Long productId;
    @Schema(description = "下单用户 ID")
    private Long ordererId;
    @Schema(description = "商品发布者用户 ID")
    private Long publisherId;
    @Schema(description = "下单时的商品标题快照")
    private String productTitleSnapshot;
    @Schema(description = "下单时的商品单价快照，单位：元")
    private BigDecimal unitPriceSnapshot;
    @Schema(description = "购买数量")
    private Integer quantity;
    @Schema(description = "订单应付金额，单位：元")
    private BigDecimal totalAmount;
    @Schema(description = "订单状态：WAITING_PAYMENT、WAITING_ACCEPTANCE、IN_PROGRESS、WAITING_CONFIRMATION、COMPLETED、CANCELLED")
    private String orderStatus;
    @Schema(description = "支付状态：UNPAID、PAID、REFUNDED")
    private String paymentStatus;
    @Schema(description = "下单时间")
    private LocalDateTime createdAt;
}
