package com.practice.ecommerce.service.customer.review;

import com.practice.ecommerce.dto.OrderedProductsResponseDto;
import com.practice.ecommerce.dto.ReviewDto;

import java.io.IOException;

public interface ReviewService {

    OrderedProductsResponseDto getOrderedProductsDetailsByOrderId(Long orderId);

    ReviewDto giveReview(ReviewDto reviewDto) throws IOException;
}
