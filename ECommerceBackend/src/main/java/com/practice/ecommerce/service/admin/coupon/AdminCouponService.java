package com.practice.ecommerce.service.admin.coupon;

import com.practice.ecommerce.entity.Coupon;

import java.util.List;

public interface AdminCouponService {
    Coupon createCoupon(Coupon coupon);
    List<Coupon> getAllCoupons();
}
