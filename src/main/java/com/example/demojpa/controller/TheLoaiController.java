package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.TheLoaiResponse;
import com.example.demojpa.entity.TheLoai;
import com.example.demojpa.repository.TheLoaiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/theloai")
@Tag(name = "03. Thể loại", description = "Quản lý thể loại sản phẩm APIs")
public class TheLoaiController {

    @Autowired
    private TheLoaiRepository theLoaiRepo;

    @Operation(summary = "Lấy danh sách thể loại", description = "API này trả về tất cả thể loại và số lượng sản phẩm trong mỗi thể loại")
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

    @Operation(summary = "Lấy thể loại theo ID", description = "API này trả về chi tiết một thể loại dựa trên ID")
    @GetMapping("/{id}")
    public ApiResponse<TheLoai> getById(
            @Parameter(description = "ID của thể loại") @PathVariable Long id) {
        TheLoai theLoai = theLoaiRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
        return new ApiResponse<>(HttpStatus.OK.value(), "Lấy thể loại thành công", theLoai);
    }

    @Operation(summary = "Tạo thể loại mới", description = "API này tạo một thể loại mới (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TheLoai> create(
            @Parameter(description = "Thông tin thể loại") @RequestBody TheLoai theLoai) {
        if (theLoaiRepo.existsByTen(theLoai.getTen())) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
                    "Thể loại với tên này đã tồn tại", null);
        }
        TheLoai saved = theLoaiRepo.save(theLoai);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo thể loại thành công", saved);
    }

    @Operation(summary = "Cập nhật thể loại", description = "API này cập nhật thông tin thể loại theo ID (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TheLoai> update(
            @Parameter(description = "ID của thể loại") @PathVariable Long id,
            @Parameter(description = "Thông tin cập nhật") @RequestBody TheLoai data) {
        TheLoai old = theLoaiRepo.findById(id).orElseThrow();
        old.setTen(data.getTen());
        TheLoai updated = theLoaiRepo.save(old);
        return new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật thể loại thành công", updated);
    }

    @Operation(summary = "Xóa thể loại", description = "API này xóa thể loại theo ID (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID của thể loại") @PathVariable Long id) {
        theLoaiRepo.deleteById(id);
        return new ApiResponse<>(HttpStatus.OK.value(), "Xóa thể loại thành công", null);
    }
}
