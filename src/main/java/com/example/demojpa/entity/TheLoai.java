package com.example.demojpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheLoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ten;

    @ManyToMany(mappedBy = "theLoais")
    @JsonBackReference // Để tránh vòng lặp vô hạn khi serialize
    private List<SanPham> sanPhams;
}
