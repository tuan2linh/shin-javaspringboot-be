package com.example.demojpa.repository;

import com.example.demojpa.entity.Cart;
import com.example.demojpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}