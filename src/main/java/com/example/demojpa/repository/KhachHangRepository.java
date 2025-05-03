package com.example.demojpa.repository;

import com.example.demojpa.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    List<KhachHang> findAllByUserId(Long userId);
}