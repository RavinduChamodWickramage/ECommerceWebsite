package com.practice.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AnalyticsResponse {

    private Long placed;

    private Long shipped;

    private Long delivered;

    private Long currentMonthOrders;

    private Long previousMonthOrders;

    private BigDecimal currentMonthEarnings;

    private BigDecimal previousMonthEarnings;
}
