package com.example.demojpa.dto;

import lombok.Data;

@Data
public class ChiTietDonHangResponse {
    private Long id;
    private String tenSanPham;
    private double donGia;
    private int soLuong;
    private double thanhTien;
}