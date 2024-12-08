package com.practice.ecommerce.controller.customer;

import com.practice.ecommerce.dto.ProfileUpdateRequest;
import com.practice.ecommerce.dto.UserDto;
import com.practice.ecommerce.entity.User;
import com.practice.ecommerce.repository.UserRepository;
import com.practice.ecommerce.service.customer.profile.ProfileService;
import com.practice.ecommerce.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    private final UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestHeader("Authorization") String token,
                                              @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);

        User user = userRepository.findFirstByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User updatedUser = profileService.updateProfile(String.valueOf(user.getId()), profileUpdateRequest);

        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String userId) {
        UserDto userDto = profileService.getProfileById(userId);
        return ResponseEntity.ok(userDto);
    }


}
