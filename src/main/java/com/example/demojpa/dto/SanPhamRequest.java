package com.example.demojpa.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class SanPhamRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String ten;

    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    private double gia;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    private String mota;

    @NotBlank(message = "Hình ảnh sản phẩm không được để trống")
    private String hinhAnh;

    @NotEmpty(message = "Phải chọn ít nhất một thể loại")
    private List<Long> theLoaiIds;
}
