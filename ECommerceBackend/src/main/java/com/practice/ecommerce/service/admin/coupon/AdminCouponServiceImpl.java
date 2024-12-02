package com.practice.ecommerce.service.admin.coupon;

import com.practice.ecommerce.entity.Coupon;
import com.practice.ecommerce.exception.ValidationException;
import com.practice.ecommerce.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCouponServiceImpl implements AdminCouponService{

    private final CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new ValidationException("Coupon with code " + coupon.getCode() + " already exists.");
        }
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
}
