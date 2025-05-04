package com.example.demojpa.controller;

import com.example.demojpa.dto.*;
import com.example.demojpa.entity.*;
import com.example.demojpa.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        try {
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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cart cart = cartService.addToCart(currentUser, request.getSanPhamId(), request.getSoLuong());

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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(@Valid @RequestBody CartItemRequest request) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cart cart = cartService.updateCartItem(currentUser, request.getSanPhamId(), request.getSoLuong());

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

            return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật số lượng thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart() {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cart cart = cartService.clearCart(currentUser);

            CartResponse response = new CartResponse();
            response.setId(cart.getId());
            response.setTotalAmount(0);
            response.setItems(List.of());

            return ResponseEntity.ok(new ApiResponse<>(200, "Xóa giỏ hàng thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/remove/{sanPhamId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(@PathVariable Long sanPhamId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cart cart = cartService.removeFromCart(currentUser, sanPhamId);

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

            return ResponseEntity.ok(new ApiResponse<>(200, "Xóa sản phẩm khỏi giỏ hàng thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}