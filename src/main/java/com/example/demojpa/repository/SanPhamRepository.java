package com.example.demojpa.repository;

import com.example.demojpa.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    Page<SanPham> findByTenContainingIgnoreCase(String ten, Pageable pageable);
}
