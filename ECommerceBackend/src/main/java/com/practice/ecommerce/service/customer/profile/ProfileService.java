package com.practice.ecommerce.service.customer.profile;

import com.practice.ecommerce.dto.ProfileUpdateRequest;
import com.practice.ecommerce.dto.UserDto;
import com.practice.ecommerce.entity.User;

public interface ProfileService {

    User updateProfile(String userId, ProfileUpdateRequest profileUpdateRequest);

    UserDto getProfileById(String userId);

}
