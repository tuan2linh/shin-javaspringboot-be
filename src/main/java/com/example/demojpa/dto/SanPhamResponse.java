package com.example.demojpa.dto;

import lombok.Data;
import java.util.List;

@Data
public class SanPhamResponse {
    private Long id;
    private String ten;
    private double gia;
    private String mota;
    private String hinhAnh;
    private List<String> tenTheLoais;
}   
