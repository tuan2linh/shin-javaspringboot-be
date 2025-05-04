package com.example.demojpa.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Data
public class DonHangRequest {
    @NotNull(message = "ID khách hàng không được để trống")
    private Long khachHangId;
    
    @NotEmpty(message = "Đơn hàng phải có ít nhất một sản phẩm")
    private List<ChiTietDonHangRequest> chiTietDonHang = new ArrayList<>();
}