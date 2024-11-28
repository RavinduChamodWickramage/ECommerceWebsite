package com.practice.ecommerce.dto;

import com.practice.ecommerce.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private UserRole role;
}
