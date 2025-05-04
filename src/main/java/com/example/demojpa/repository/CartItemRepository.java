package com.example.demojpa.repository;

import com.example.demojpa.entity.CartItem;
import com.example.demojpa.entity.Cart;
import com.example.demojpa.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndSanPham(Cart cart, SanPham sanPham);
}