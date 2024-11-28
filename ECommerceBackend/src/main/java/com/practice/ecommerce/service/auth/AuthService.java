package com.practice.ecommerce.service.auth;

import com.practice.ecommerce.dto.SignupRequest;
import com.practice.ecommerce.dto.UserDto;

public interface AuthService {

    UserDto createUser(SignupRequest signupRequest);

    boolean hasUserWithEmail(String email);

    void createAdminAccount();
}
