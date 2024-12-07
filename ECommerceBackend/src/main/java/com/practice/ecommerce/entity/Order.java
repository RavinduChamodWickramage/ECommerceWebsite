package com.practice.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.practice.ecommerce.dto.CartItemsDto;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private Long amount;

    private String address;

    private OrderStatus orderStatus;

    private Long totalAmount;

    private Long discount;

    private UUID trackingId;

    private String orderDescription;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "coupon_id", referencedColumnName = "id")
    private Coupon coupon;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @JsonManagedReference
    private List<CartItems> cartItems;

    public OrderDto getOrderDto() {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(id);
        orderDto.setAddress(address);
        orderDto.setTrackingId(trackingId);
        orderDto.setAmount(amount);
        orderDto.setTotalAmount(totalAmount);
        orderDto.setDiscount(discount);
        orderDto.setDate(date);
        orderDto.setOrderStatus(orderStatus);
        orderDto.setUserName(user.getName());
        orderDto.setOrderDescription(orderDescription);
        orderDto.setCartItems(new ArrayList<>()); // Initialize cartItems list

        if (coupon != null) {
            orderDto.setCouponName(coupon.getName());
            orderDto.setDiscountRate(coupon.getDiscount());
        }

        return orderDto;
    }
}
