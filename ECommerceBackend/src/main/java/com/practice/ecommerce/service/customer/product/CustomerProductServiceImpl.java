package com.practice.ecommerce.service.customer.product;

import com.practice.ecommerce.dto.ProductDetailDto;
import com.practice.ecommerce.dto.ProductDto;
import com.practice.ecommerce.entity.FAQ;
import com.practice.ecommerce.entity.Product;
import com.practice.ecommerce.entity.Review;
import com.practice.ecommerce.repository.FAQRepository;
import com.practice.ecommerce.repository.ProductRepository;
import com.practice.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerProductServiceImpl implements CustomerProductService{

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    private final FAQRepository faqRepository;

    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public List<ProductDto> searchProductByName(String name) {
        List<Product> products = productRepository.findAllByNameContaining(name);
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    @Override
    public ProductDetailDto getProductDetailById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            List<FAQ> faqList = faqRepository.findAllByProductId(productId);
            List<Review> reviewList = reviewRepository.findAllByProductId(productId);

            ProductDetailDto productDetailDto = new ProductDetailDto();
            productDetailDto.setProductDto(optionalProduct.get().getDto());
            productDetailDto.setFaqDtoList(faqList.stream().map(FAQ::getFAQDto).collect(Collectors.toList()));
            productDetailDto.setReviewDtoList(reviewList.stream().map(Review::getDto).collect(Collectors.toList()));

            return productDetailDto;
        }

        return null;
    }
}
