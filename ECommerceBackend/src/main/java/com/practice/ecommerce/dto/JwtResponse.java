package com.practice.ecommerce.dto;

import com.practice.ecommerce.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;

    private Long userId;

    private UserRole role;

}
