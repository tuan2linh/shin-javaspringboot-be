package com.example.demojpa.dto;

import lombok.Data;
import java.util.List;

@Data
public class SanPhamResponse {
    private Long id;
    private String ten;
    private double gia;
    private List<String> tenTheLoais;
}   
