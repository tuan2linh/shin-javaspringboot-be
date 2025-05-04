package com.example.demojpa.controller;

import com.example.demojpa.dto.*;
import com.example.demojpa.entity.*;
import com.example.demojpa.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartService.getOrCreateCart(currentUser);
        
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalAmount(cart.getTotalAmount());
        
        List<CartItemResponse> items = cart.getCartItems().stream()
            .map(item -> {
                CartItemResponse itemRes = new CartItemResponse();
                itemRes.setId(item.getId());
                itemRes.setSanPhamId(item.getSanPham().getId());
                itemRes.setTenSanPham(item.getSanPham().getTen());
                itemRes.setDonGia(item.getSanPham().getGia());
                itemRes.setSoLuong(item.getSoLuong());
                itemRes.setThanhTien(item.getSanPham().getGia() * item.getSoLuong());
                return itemRes;
            })
            .collect(Collectors.toList());
        
        response.setItems(items);

        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy giỏ hàng thành công", response));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @RequestParam Long sanPhamId,
            @RequestParam int soLuong) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartService.addToCart(currentUser, sanPhamId, soLuong);
        
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalAmount(cart.getTotalAmount());
        
        List<CartItemResponse> items = cart.getCartItems().stream()
            .map(item -> {
                CartItemResponse itemRes = new CartItemResponse();
                itemRes.setId(item.getId());
                itemRes.setSanPhamId(item.getSanPham().getId());
                itemRes.setTenSanPham(item.getSanPham().getTen());
                itemRes.setDonGia(item.getSanPham().getGia());
                itemRes.setSoLuong(item.getSoLuong());
                itemRes.setThanhTien(item.getSanPham().getGia() * item.getSoLuong());
                return itemRes;
            })
            .collect(Collectors.toList());
        
        response.setItems(items);

        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm vào giỏ hàng thành công", response));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @RequestParam Long sanPhamId,
            @RequestParam int soLuong) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartService.updateCartItem(currentUser, sanPhamId, soLuong);
        
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalAmount(cart.getTotalAmount());
        
        List<CartItemResponse> items = cart.getCartItems().stream()
            .map(item -> {
                CartItemResponse itemRes = new CartItemResponse();
                itemRes.setId(item.getId());
                itemRes.setSanPhamId(item.getSanPham().getId());
                itemRes.setTenSanPham(item.getSanPham().getTen());
                itemRes.setDonGia(item.getSanPham().getGia());
                itemRes.setSoLuong(item.getSoLuong());
                itemRes.setThanhTien(item.getSanPham().getGia() * item.getSoLuong());
                return itemRes;
            })
            .collect(Collectors.toList());
        
        response.setItems(items);

        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật giỏ hàng thành công", response));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartService.clearCart(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa giỏ hàng thành công", null));
    }
}