package com.practice.ecommerce.service.admin.order;

import com.practice.ecommerce.dto.AnalyticsResponse;
import com.practice.ecommerce.dto.OrderDto;

import java.math.BigDecimal;
import java.util.List;

public interface AdminOrderService {

    List<OrderDto> getAllPlacedOrders();

    OrderDto changeOrderStatus(Long orderId, String orderStatus);

    AnalyticsResponse calculateAnalytics();

    Long getTotalOrdersForMonth(int month, int year);

    BigDecimal getTotalEarningsForMonth(int month, int year);
}
