package com.example.demojpa.dto;

import lombok.Data;
import java.util.List;

@Data
public class DonHangRequest {
    private Long khachHangId; // hoặc lấy từ user nếu đã liên kết
    private List<ChiTietDonHangRequest> chiTiet;
}
