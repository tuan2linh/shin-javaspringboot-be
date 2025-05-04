package com.example.demojpa.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long sanPhamId;
    private String tenSanPham;
    private double donGia;
    private int soLuong;
    private double thanhTien;
}