package com.example.demojpa.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class KhachHangRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 3, message = "Họ tên phải từ 3 ký tự trở lên")
    private String hoTen;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải bắt đầu bằng 0 và đủ 10 số")
    private String soDienThoai;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;
}
