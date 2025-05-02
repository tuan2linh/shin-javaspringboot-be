package com.example.demojpa.dto;

import lombok.Data;

import java.util.List;

@Data
public class SanPhamRequest {
    private String ten;
    private double gia;
    private String mota;
    private String hinhAnh;
    private List<Long> theLoaiIds;
}
