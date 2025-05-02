package com.example.demojpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ten;
    private double gia;
    @Column(length = 1000)
    private String mota;
    private String hinhAnh;
    @ManyToMany
    @JoinTable(
    name = "san_pham_the_loai",
    joinColumns = @JoinColumn(name = "san_pham_id"),
    inverseJoinColumns = @JoinColumn(name = "the_loai_id")
    )
    @JsonBackReference // Để tránh vòng lặp vô hạn khi serialize
    private List<TheLoai> theLoais;
}
