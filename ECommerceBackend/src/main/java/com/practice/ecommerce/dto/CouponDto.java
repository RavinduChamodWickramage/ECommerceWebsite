package com.practice.ecommerce.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class CouponDto {

    private Long id;

    private String name;

    private String code;

    private Long discount;

    private Date expirationDate;
}
