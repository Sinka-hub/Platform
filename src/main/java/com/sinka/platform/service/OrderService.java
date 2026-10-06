package com.sinka.platform.service;

import com.sinka.platform.DTO.OrderCreateRequest;
import com.sinka.platform.VO.OrderCreateVO;
import com.sinka.platform.VO.OrderDetailVO;
import com.sinka.platform.VO.OrderListVO;
import com.sinka.platform.VO.PageResultVO;
import jakarta.validation.Valid;

public interface OrderService {
    OrderCreateVO createOrder(@Valid OrderCreateRequest request);

    PageResultVO<OrderListVO> getMyOrders(Integer pageNum, Integer pageSize);

    OrderDetailVO getOrderDetail(Long id);

    boolean payOrder(Long id);

    boolean acceptOrder(Long id);

    boolean submitOrder(Long id);

    boolean confirmOrder(Long id);

    boolean cancelOrder(Long id);

    void closeTimeoutOrder(Long aLong);

    int closeExpiredUnpaidOrders();
}
