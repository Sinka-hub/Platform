package com.sinka.platform.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sinka.platform.DTO.OrderCreateRequest;
import com.sinka.platform.VO.*;
import com.sinka.platform.constant.OrderStatusConstant;
import com.sinka.platform.constant.PaymentStatusConstant;
import com.sinka.platform.entity.Order;
import com.sinka.platform.mapper.CoinTransactionMapper;
import com.sinka.platform.mapper.CoinWalletMapper;
import com.sinka.platform.mapper.OrderMapper;
import com.sinka.platform.mapper.ProductMapper;
import com.sinka.platform.service.OrderService;
import com.sinka.platform.utils.RedisIdWorker;
import com.sinka.platform.utils.ThreadLocalIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Autowired
    private CoinWalletMapper coinWalletMapper;

    @Autowired
    private CoinTransactionMapper coinTransactionMapper;

    @Override
    public OrderCreateVO createOrder(OrderCreateRequest request) {
        Long buyerId = ThreadLocalIdUtil.getUserId();

        ProductDetailVO product =
                productMapper.selectPublishedProductById(request.getProductId());
        // 检查商品是否存在且不是发布者
        if (product == null || product.getPublisherId().equals(buyerId)) {
            return null;
        }

        Order order = new Order();
        order.setOrderNo(String.valueOf(redisIdWorker.nextId("order")));
        order.setProductId(product.getId());
        order.setOrdererId(buyerId);
        order.setPublisherId(product.getPublisherId());
        order.setProductTitleSnapshot(product.getTitle());
        order.setUnitPriceSnapshot(product.getPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(
                product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setOrderStatus(OrderStatusConstant.WAITING_PAYMENT);
        order.setPaymentStatus(PaymentStatusConstant.UNPAID);

        orderMapper.insertOrder(order);

        return new OrderCreateVO(order.getId(), order.getOrderNo());
    }

    @Override
    public PageResultVO<OrderListVO> getMyOrders(Integer pageNum, Integer pageSize) {
        Long userId = ThreadLocalIdUtil.getUserId();

        PageHelper.startPage(pageNum, pageSize);
        Page<OrderListVO> orders =
                (Page<OrderListVO>) orderMapper.selectMyOrders(userId);

        PageResultVO<OrderListVO> result = new PageResultVO<>();
        result.setRecords(orders.getResult());
        result.setPages(orders.getPages());

        return result;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        Long userId = ThreadLocalIdUtil.getUserId();
        return orderMapper.selectOrderDetailById(id, userId);
    }

    @Override
    @Transactional
    public boolean payOrder(Long id) {
        Long buyerId = ThreadLocalIdUtil.getUserId();

        // 查询订单并加锁，避免同一订单被同时支付
        Order order = orderMapper.selectOrderForUpdate(id);

        if (order == null
                || !buyerId.equals(order.getOrdererId())
                || !OrderStatusConstant.WAITING_PAYMENT.equals(order.getOrderStatus())
                || !PaymentStatusConstant.UNPAID.equals(order.getPaymentStatus())) {
            return false;
        }

        BigDecimal amount = order.getTotalAmount();

        // 余额不足时不扣款
        if (coinWalletMapper.subtractBalance(buyerId, amount) != 1) {
            return false;
        }

        // 已经扣款，后续失败必须抛异常，让事务回滚
        if (orderMapper.payOrder(id, buyerId) != 1) {
            throw new IllegalStateException("订单支付状态更新失败");
        }

        coinTransactionMapper.insertTransaction(
                buyerId,
                id,
                "ORDER_PAY",
                amount.negate(),
                "PAY:" + id);

        return true;
    }

    @Override
    public boolean acceptOrder(Long id) {
        Long publisherId = ThreadLocalIdUtil.getUserId();
        return orderMapper.acceptOrder(id, publisherId) == 1;
    }

    @Override
    public boolean submitOrder(Long id) {
        Long publisherId = ThreadLocalIdUtil.getUserId();
        return orderMapper.submitOrder(id, publisherId) == 1;
    }

    @Override
    @Transactional
    public boolean confirmOrder(Long id) {
        Long buyerId = ThreadLocalIdUtil.getUserId();

        Order order = orderMapper.selectOrderForUpdate(id);

        if (order == null
                || !buyerId.equals(order.getOrdererId())
                || !"WAITING_CONFIRMATION".equals(order.getOrderStatus())
                || !PaymentStatusConstant.PAID.equals(order.getPaymentStatus())) {
            return false;
        }

        // 将订单改为已完成
        if (orderMapper.confirmOrder(id, buyerId) != 1) {
            throw new IllegalStateException("订单完成状态更新失败");
        }

        // 给发布者增加金币
        if (coinWalletMapper.addBalance(
                order.getPublisherId(), order.getTotalAmount()) != 1) {
            throw new IllegalStateException("发布者钱包不存在");
        }

        // 记录发布者的收入流水
        coinTransactionMapper.insertTransaction(
                order.getPublisherId(),
                id,
                "ORDER_SETTLE",
                order.getTotalAmount(),
                "SETTLE:" + id);

        return true;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long id) {
        Long buyerId = ThreadLocalIdUtil.getUserId();

        Order order = orderMapper.selectOrderForUpdate(id);

        // 只有买家能取消已支付、尚未被接取的订单
        if (order == null
                || !buyerId.equals(order.getOrdererId())
                || !OrderStatusConstant.WAITING_ACCEPTANCE.equals(order.getOrderStatus())
                || !PaymentStatusConstant.PAID.equals(order.getPaymentStatus())) {
            return false;
        }

        if (orderMapper.cancelOrder(id, buyerId) != 1) {
            throw new IllegalStateException("订单取消状态更新失败");
        }

        // 退回买家钱包
        if (coinWalletMapper.addBalance(
                buyerId, order.getTotalAmount()) != 1) {
            throw new IllegalStateException("买家钱包不存在");
        }

        // 记录退款收入
        coinTransactionMapper.insertTransaction(
                buyerId,
                id,
                "ORDER_REFUND",
                order.getTotalAmount(),
                "REFUND:" + id);

        return true;
    }


}
