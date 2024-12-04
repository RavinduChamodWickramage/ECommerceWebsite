package com.practice.ecommerce.dto;

import com.practice.ecommerce.enums.OrderStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {

    private Long id;

    private Date date;

    private Long amount;

    private String address;

    private OrderStatus orderStatus;

    private Long totalAmount;

    private Long discount;

    private UUID trackingId;

    private String userName;

    private List<CartItemsDto> cartItems;

    private String couponName;

    private Long discountRate;

    private String orderDescription;
}
