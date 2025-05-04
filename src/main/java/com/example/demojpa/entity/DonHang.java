package com.example.demojpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate ngayDat;
    private double tongTien;

    // Mỗi đơn hàng thuộc 1 khách hàng
    @ManyToOne
    @JoinColumn(name = "khach_hang_id") // tên cột FK
    @JsonBackReference // tránh vòng lặp vô hạn khi serialize
    private KhachHang khachHang;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonHang> chiTietDonHangList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TrangThaiDonHang trangThai = TrangThaiDonHang.PENDING;

    private boolean isHidden = false;
}
