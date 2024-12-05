package com.practice.ecommerce.service.admin.order;

import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.dto.ProductDto;
import com.practice.ecommerce.entity.Order;
import com.practice.ecommerce.entity.Product;
import com.practice.ecommerce.enums.OrderStatus;
import com.practice.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService{

    private final OrderRepository orderRepository;

    @Override
    public List<OrderDto> getAllPlacedOrders() {
        List<Order> orderList = orderRepository.findAllByOrderStatusIn(List.of(OrderStatus.PLACED, OrderStatus.SHIPPED,
                OrderStatus.DELIVERED));

        return orderList.stream().map(Order::getOrderDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto changeOrderStatus(Long orderId, String orderStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (Objects.equals(orderStatus, "SHIPPED")) {
                order.setOrderStatus(OrderStatus.SHIPPED);
            } else if (Objects.equals(orderStatus, "DELIVERED")) {
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
            return orderRepository.save(order).getOrderDto();
        }
        return null;
    }


}
