package com.example.demojpa.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DonHangResponse {
    private Long id;
    private LocalDate ngayDat;
    private double tongTien;
    private String tenKhach;
}
