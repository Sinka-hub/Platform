package com.sinka.platform.mapper;

import com.sinka.platform.VO.OrderDetailVO;
import com.sinka.platform.VO.OrderListVO;
import com.sinka.platform.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insertOrder(Order order);

    List<OrderListVO> selectMyOrders(Long userId);

    OrderDetailVO selectOrderDetailById(Long id, Long userId);

    int payOrder(Long id, Long buyerId);

    int acceptOrder(Long id, Long publisherId);

    int submitOrder(Long id, Long publisherId);

    int confirmOrder(Long id, Long buyerId);

    Order selectOrderForUpdate(Long id);

    int cancelOrder(Long id, Long buyerId);

    int closeTimeoutOrder(Long id);

    int closeExpiredUnpaidOrders();
}
