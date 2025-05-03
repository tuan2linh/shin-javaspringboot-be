package com.example.demojpa.dto;

import lombok.Data;
import java.util.List;

@Data
public class KhachHangResponse {
    private Long id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private List<DonHangResponse> donHangs;
}