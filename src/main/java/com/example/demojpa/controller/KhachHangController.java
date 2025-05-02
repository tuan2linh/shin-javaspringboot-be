package com.example.demojpa.controller;

import com.example.demojpa.dto.KhachHangRequest;
import com.example.demojpa.entity.KhachHang;
import com.example.demojpa.repository.KhachHangRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangRepository repo;

    @GetMapping
    public List<KhachHang> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public KhachHang getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public KhachHang themKhachHang(@RequestBody @Valid KhachHangRequest req) {
        // Nếu tới đây, dữ liệu đã hợp lệ (đã pass validation)
        KhachHang kh = new KhachHang();
        kh.setHoTen(req.getHoTen());
        kh.setEmail(req.getEmail());
        kh.setSoDienThoai(req.getSoDienThoai());
        return repo.save(kh); // Lưu vào database và trả về entity đã được tạo
    }

    @PutMapping("/{id}")
    public KhachHang update(@PathVariable Long id, @RequestBody KhachHang newData) {
        KhachHang kh = repo.findById(id).orElseThrow();
        kh.setHoTen(newData.getHoTen());
        kh.setEmail(newData.getEmail());
        kh.setSoDienThoai(newData.getSoDienThoai());
        return repo.save(kh);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
