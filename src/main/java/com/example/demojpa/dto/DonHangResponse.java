package com.example.demojpa.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DonHangResponse {
    private Long id;
    private LocalDate ngayDat;
    private double tongTien;
    private String tenKhach;
    private List<ChiTietDonHangResponse> chiTietDonHang;
}
