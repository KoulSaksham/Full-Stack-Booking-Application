package com.saksham.booking_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saksham.booking_application.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
