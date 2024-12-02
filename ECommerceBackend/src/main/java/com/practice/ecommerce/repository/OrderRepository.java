package com.practice.ecommerce.repository;

import com.practice.ecommerce.entity.Order;
import com.practice.ecommerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);
}
