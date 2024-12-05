package com.practice.ecommerce.service.admin.faq;

import com.practice.ecommerce.dto.FAQDto;

public interface FAQService {

    FAQDto postFAQ(Long productId, FAQDto faqDto);
}
