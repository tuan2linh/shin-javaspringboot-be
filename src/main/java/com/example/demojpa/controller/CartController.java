package com.example.demojpa.controller;

import com.example.demojpa.dto.*;
import com.example.demojpa.entity.*;
import com.example.demojpa.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
@Tag(name = "04. Giỏ hàng", description = "Quản lý giỏ hàng APIs - Mỗi user có một giỏ hàng riêng")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "Xem giỏ hàng", description = "API này trả về thông tin giỏ hàng của user hiện tại (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "Thêm sản phẩm vào giỏ", description = "API này thêm một sản phẩm vào giỏ hàng hoặc tăng số lượng nếu đã tồn tại (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Parameter(description = "Thông tin sản phẩm cần thêm") @Valid @RequestBody CartItemRequest request) {
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

    @Operation(summary = "Cập nhật số lượng", description = "API này cập nhật số lượng của một sản phẩm trong giỏ hàng (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @Parameter(description = "Thông tin cập nhật số lượng") @Valid @RequestBody CartItemRequest request) {
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

    @Operation(summary = "Xóa tất cả sản phẩm", description = "API này xóa tất cả sản phẩm trong giỏ hàng (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "Xóa một sản phẩm", description = "API này xóa một sản phẩm khỏi giỏ hàng (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/remove/{sanPhamId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @Parameter(description = "ID của sản phẩm cần xóa") @PathVariable Long sanPhamId) {
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