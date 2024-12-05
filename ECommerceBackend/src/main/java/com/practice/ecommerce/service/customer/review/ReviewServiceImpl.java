package com.practice.ecommerce.service.customer.review;

import com.practice.ecommerce.dto.OrderedProductsResponseDto;
import com.practice.ecommerce.dto.ProductDto;
import com.practice.ecommerce.dto.ReviewDto;
import com.practice.ecommerce.entity.*;
import com.practice.ecommerce.repository.OrderRepository;
import com.practice.ecommerce.repository.ProductRepository;
import com.practice.ecommerce.repository.ReviewRepository;
import com.practice.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;


    @Override
    public OrderedProductsResponseDto getOrderedProductsDetailsByOrderId(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderedProductsResponseDto orderedProductsResponseDto = new OrderedProductsResponseDto();

        if (optionalOrder.isPresent()) {
            orderedProductsResponseDto.setOrderAmount(optionalOrder.get().getAmount());

            List<ProductDto> productDtoList = new ArrayList<>();

            for (CartItems cartItems : optionalOrder.get().getCartItems()) {
                ProductDto productDto = new ProductDto();

                productDto.setId(cartItems.getProduct().getId());
                productDto.setName(cartItems.getProduct().getName());
                productDto.setPrice(cartItems.getProduct().getPrice());
                productDto.setQuantity(cartItems.getQuantity());
                productDto.setDescription(cartItems.getProduct().getDescription());
                productDto.setByteImg(cartItems.getProduct().getImg() != null ? Base64.getEncoder().encodeToString(cartItems.getProduct().getImg()) : null);
                productDto.setCategoryId(cartItems.getProduct().getCategory().getId());
                productDto.setCategoryName(cartItems.getProduct().getCategory().getName());

                productDtoList.add(productDto);
            }

            orderedProductsResponseDto.setProductDtoList(productDtoList);

        }

        return orderedProductsResponseDto;
    }

    @Override
    public ReviewDto giveReview(ReviewDto reviewDto) throws IOException {
        Optional<Product> optionalProduct = productRepository.findById(reviewDto.getProductId());
        Optional<User> optionalUser = userRepository.findById(reviewDto.getUserId());

        if (optionalProduct.isPresent() && optionalUser.isPresent()) {
            Review review = new Review();

            review.setRating(reviewDto.getRating());
            review.setDescription(reviewDto.getDescription());
            review.setProduct(optionalProduct.get());
            review.setUser(optionalUser.get());
            review.setImg(reviewDto.getImg().getBytes());

            return reviewRepository.save(review).getDto();
        }
        return null;
    }
}
