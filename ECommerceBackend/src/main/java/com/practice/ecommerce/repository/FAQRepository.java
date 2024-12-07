package com.practice.ecommerce.repository;

import com.practice.ecommerce.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {


    List<FAQ> findAllByProductId(Long productId);
}
