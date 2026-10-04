package com.sinka.platform.controller;

import com.sinka.platform.DTO.OrderCreateRequest;
import com.sinka.platform.VO.OrderCreateVO;
import com.sinka.platform.VO.OrderDetailVO;
import com.sinka.platform.VO.OrderListVO;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.entity.Result;
import com.sinka.platform.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单", description = "订单相关接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    // 创建订单
    @PostMapping
    @Operation(summary = "创建订单", description = "购买已发布商品，订单初始状态为待支付")
    public Result<OrderCreateVO> createOrder(
            @Valid @RequestBody OrderCreateRequest request) {
        OrderCreateVO order = orderService.createOrder(request);

        if (order == null) {
            return Result.error("商品不存在、未发布或不能购买自己的商品");
        }

        return Result.success(order);
    }

    // 获取我的订单
    @Operation(summary = "分页查询我的订单", description = "返回当前用户作为买家或发布者参与的订单")
    @GetMapping
    public Result<PageResultVO<OrderListVO>> getMyOrders(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResultVO<OrderListVO> result =
                orderService.getMyOrders(pageNum, pageSize);
        return Result.success(result);
    }

    // 获取订单详情
    @Operation(summary = "查询订单详情", description = "仅订单买家或发布者可以查看")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@Parameter(description = "订单 ID") @PathVariable Long id) {
        OrderDetailVO order = orderService.getOrderDetail(id);

        if (order == null) {
            return Result.error("订单不存在或无权查看");
        }

        return Result.success(order);
    }

    // 支付订单
    @Operation(summary = "金币支付订单", description = "从买家钱包扣除订单金额，成功后进入待接取状态")
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@Parameter(description = "订单 ID") @PathVariable Long id) {
        boolean paid = orderService.payOrder(id);

        if (!paid) {
            return Result.error("订单不存在、无权支付、状态不正确或金币余额不足");
        }

        return Result.success();
    }

    // 发布者接取订单
    @Operation(summary = "发布者接取订单", description = "发布者接取已支付且待接取的订单")
    @PostMapping("/{id}/accept")
    public Result<Void> acceptOrder(@Parameter(description = "订单 ID") @PathVariable Long id) {
        boolean accepted = orderService.acceptOrder(id);

        if (!accepted) {
            return Result.error("订单不存在、无权接取或订单状态不正确");
        }

        return Result.success();
    }
    //发布者点击完成订单(定单状态由in_progress变更为waiting_confirmation)
    @Operation(summary = "发布者提交完成", description = "发布者提交进行中的订单，等待买家验收")
    @PostMapping("/{id}/submit")
    public Result submitOrder(@Parameter(description = "订单 ID") @PathVariable Long id) {
        boolean success = orderService.submitOrder(id);
        if (!success) {
            return Result.error("订单不存在、状态不正确或无权操作");
        }
        return Result.success();
    }
    //买家点击确认完成(定单状态由waiting_confirmation变更为completed)
    @Operation(summary = "买家验收订单", description = "买家确认完成后，将订单金币结算到发布者钱包")
    @PostMapping("/{id}/confirm")
    public Result confirmOrder(@Parameter(description = "订单 ID") @PathVariable Long id) {
        boolean success = orderService.confirmOrder(id);
        if (!success) {
            return Result.error("订单不存在、状态不正确或无权操作");
        }
        return Result.success();
    }

    @Operation(summary = "取消订单并退款", description = "买家可在发布者接取前取消已支付订单，金币退回买家钱包")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单 ID") @PathVariable Long id) {
        boolean cancelled = orderService.cancelOrder(id);

        if (!cancelled) {
            return Result.error("订单不存在、无权取消或订单状态不允许取消");
        }

        return Result.success();
    }

}
