package com.practice.ecommerce.controller.admin;

import com.practice.ecommerce.dto.AnalyticsResponse;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.service.admin.order.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/placed-orders")
    public ResponseEntity<List<OrderDto>> getAllPlacedOrders() {
        return ResponseEntity.ok(adminOrderService.getAllPlacedOrders());
    }

    @GetMapping("/order/{orderId}/{orderStatus}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable Long orderId, @PathVariable String orderStatus) {
        OrderDto orderDto = adminOrderService.changeOrderStatus(orderId, orderStatus);

        if (orderDto == null)
            return new ResponseEntity<>("Order not found", HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

    @GetMapping("/order/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(adminOrderService.calculateAnalytics());
    }

}
