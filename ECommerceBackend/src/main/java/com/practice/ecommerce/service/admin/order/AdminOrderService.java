package com.practice.ecommerce.service.admin.order;

import com.practice.ecommerce.dto.OrderDto;

import java.util.List;

public interface AdminOrderService {

    List<OrderDto> getAllPlacedOrders();

    OrderDto changeOrderStatus(Long orderId, String orderStatus);
}
