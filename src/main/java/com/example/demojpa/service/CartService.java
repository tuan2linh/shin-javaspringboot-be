package com.example.demojpa.service;

import com.example.demojpa.entity.*;
import com.example.demojpa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    @Autowired
    private SanPhamRepository sanPhamRepo;

    public Cart getOrCreateCart(User user) {
        return cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });
    }

    @Transactional
    public Cart addToCart(User user, Long sanPhamId, int soLuong) {
        Cart cart = getOrCreateCart(user);
        SanPham sanPham = sanPhamRepo.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cartItemRepo.findByCartAndSanPham(cart, sanPham);

        if (existingItem.isPresent()) {
            // If product exists, update quantity
            CartItem item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            cartItemRepo.save(item);
        } else {
            // If product doesn't exist, create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setSanPham(sanPham);
            newItem.setSoLuong(soLuong);
            cart.getCartItems().add(newItem);
        }

        return cartRepo.save(cart);
    }

    @Transactional
    public Cart updateCartItem(User user, Long sanPhamId, int soLuong) {
        Cart cart = getOrCreateCart(user);
        SanPham sanPham = sanPhamRepo.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        CartItem item = cartItemRepo.findByCartAndSanPham(cart, sanPham)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));

        if (soLuong <= 0) {
            cart.getCartItems().remove(item);
            cartItemRepo.delete(item);
        } else {
            item.setSoLuong(soLuong);
            cartItemRepo.save(item);
        }

        return cartRepo.save(cart);
    }

    @Transactional
    public Cart clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        return cartRepo.save(cart);
    }

    @Transactional
    public Cart removeFromCart(User user, Long sanPhamId) {
        Cart cart = getOrCreateCart(user);
        SanPham sanPham = sanPhamRepo.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        CartItem item = cartItemRepo.findByCartAndSanPham(cart, sanPham)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));

        cart.getCartItems().remove(item);
        cartItemRepo.delete(item);

        return cartRepo.save(cart);
    }
}