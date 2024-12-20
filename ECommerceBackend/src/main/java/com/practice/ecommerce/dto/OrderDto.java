package com.practice.ecommerce.dto;

import com.practice.ecommerce.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {

    private Long id;

    private Date date;

    private BigDecimal amount;

    private String address;

    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    private BigDecimal discount;

    private UUID trackingId;

    private String userName;

    private List<CartItemsDto> cartItems;

    private String couponName;

    private BigDecimal discountRate;

    private String orderDescription;
}
