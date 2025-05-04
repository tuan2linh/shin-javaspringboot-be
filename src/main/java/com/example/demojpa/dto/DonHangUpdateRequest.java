package com.example.demojpa.dto;

import com.example.demojpa.entity.TrangThaiDonHang;
import lombok.Data;

@Data
public class DonHangUpdateRequest {
    private TrangThaiDonHang trangThai;
    private Boolean isHidden;
}