package com.practice.ecommerce.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String name;

    private String email;

    private String password;

}
