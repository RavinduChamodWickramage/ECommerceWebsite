package com.practice.ecommerce.service.customer.profile;

import com.practice.ecommerce.dto.ProfileUpdateRequest;
import com.practice.ecommerce.dto.UserDto;
import com.practice.ecommerce.entity.User;
import com.practice.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User updateProfile(String userId, ProfileUpdateRequest profileUpdateRequest) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (profileUpdateRequest.getName() != null) {
            user.setName(profileUpdateRequest.getName());
        }
        if (profileUpdateRequest.getEmail() != null) {
            user.setEmail(profileUpdateRequest.getEmail());
        }
        if (profileUpdateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(profileUpdateRequest.getPassword()));
        }

        return userRepository.save(user);
    }


    @Override
    public UserDto getProfileById(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setRole(user.getRole());
        return userDto;

    }
}
