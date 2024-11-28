package com.practice.ecommerce.repository;

import com.practice.ecommerce.entity.User;
import com.practice.ecommerce.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String username);

    User findByRole(UserRole userRole);
}
