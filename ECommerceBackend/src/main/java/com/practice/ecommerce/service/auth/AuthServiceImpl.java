package com.practice.ecommerce.service.auth;

import com.practice.ecommerce.dto.SignupRequest;
import com.practice.ecommerce.dto.UserDto;
import com.practice.ecommerce.entity.Order;
import com.practice.ecommerce.entity.User;
import com.practice.ecommerce.enums.OrderStatus;
import com.practice.ecommerce.enums.UserRole;
import com.practice.ecommerce.repository.OrderRepository;
import com.practice.ecommerce.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OrderRepository orderRepository;

    public UserDto createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        User createdUser = userRepository.save(user);

        Order order = new Order();
        order.setAmount(0L);
        order.setTotalAmount(0L);
        order.setDiscount(0L);
        order.setUser(createdUser);
        order.setOrderStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());

        return userDto;
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @Override
    @PostConstruct
    public void createAdminAccount() {
        User adminAccount = userRepository.findByRole(UserRole.ADMIN);

        if(adminAccount == null) {
            User user = new User();
            user.setEmail("admin@test.com");
            user.setName("admin");
            user.setRole(UserRole.ADMIN);
            user.setPassword(bCryptPasswordEncoder.encode("admin"));
            userRepository.save(user);
        }
    }


}
