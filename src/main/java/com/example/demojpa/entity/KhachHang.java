package com.example.demojpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;

    // Một khách hàng có nhiều đơn hàng
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL)
    private List<DonHang> donHangs;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
