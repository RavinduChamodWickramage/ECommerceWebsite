package com.practice.ecommerce.repository;

import com.practice.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findAllByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
