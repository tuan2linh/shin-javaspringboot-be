package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.TheLoaiResponse;
import com.example.demojpa.entity.TheLoai;
import com.example.demojpa.repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/theloai")
public class TheLoaiController {

    @Autowired
    private TheLoaiRepository theLoaiRepo;

    @GetMapping
    public ApiResponse<List<TheLoaiResponse>> getAll() {
        List<TheLoaiResponse> responses = theLoaiRepo.findAll().stream().map(tl -> {
            TheLoaiResponse res = new TheLoaiResponse();
            res.setId(tl.getId());
            res.setTen(tl.getTen());
            res.setSoLuongSanPham(
                    tl.getSanPhams() != null ? tl.getSanPhams().size() : 0);
            return res;
        }).collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách thể loại thành công", responses);
    }

    @GetMapping("/{id}")
    public ApiResponse<TheLoai> getById(@PathVariable Long id) {
        TheLoai theLoai = theLoaiRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
        return new ApiResponse<>(HttpStatus.OK.value(), "Lấy thể loại thành công", theLoai);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TheLoai> create(@RequestBody TheLoai theLoai) {
        if (theLoaiRepo.existsByTen(theLoai.getTen())) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
                    "Thể loại với tên này đã tồn tại", null);
        }
        TheLoai saved = theLoaiRepo.save(theLoai);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo thể loại thành công", saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TheLoai> update(@PathVariable Long id, @RequestBody TheLoai data) {
        TheLoai old = theLoaiRepo.findById(id).orElseThrow();
        old.setTen(data.getTen());
        TheLoai updated = theLoaiRepo.save(old);
        return new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật thể loại thành công", updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        theLoaiRepo.deleteById(id);
        return new ApiResponse<>(HttpStatus.OK.value(), "Xóa thể loại thành công", null);
    }
}
