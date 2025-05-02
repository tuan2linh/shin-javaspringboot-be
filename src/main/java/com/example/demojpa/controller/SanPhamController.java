package com.example.demojpa.controller;

import com.example.demojpa.dto.SanPhamRequest;
import com.example.demojpa.dto.SanPhamResponse;
import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.PageResponse;
import com.example.demojpa.entity.SanPham;
import com.example.demojpa.entity.TheLoai;
import com.example.demojpa.repository.SanPhamRepository;
import com.example.demojpa.repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sanpham")
public class SanPhamController {

    @Autowired
    private SanPhamRepository repo;

    @Autowired
    private TheLoaiRepository theLoaiRepo;

    @GetMapping
    public List<SanPhamResponse> getAll() {
        return repo.findAll().stream().map(sp -> {
            SanPhamResponse res = new SanPhamResponse();
            res.setId(sp.getId());
            res.setTen(sp.getTen());
            res.setGia(sp.getGia());
            res.setMota(sp.getMota());
            res.setHinhAnh(sp.getHinhAnh());
            List<String> tenTheLoais = sp.getTheLoais().stream()
                    .map(TheLoai::getTen)
                    .collect(Collectors.toList());
            res.setTenTheLoais(tenTheLoais);
            return res;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SanPhamResponse>> getSanPhamById(@PathVariable Long id) {
        // SanPham sp = repo.findById(id)
        // .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm id:
        // " + id));
        SanPhamResponse sp = repo.findById(id).map(sanPham -> {
            SanPhamResponse res = new SanPhamResponse();
            res.setId(sanPham.getId());
            res.setTen(sanPham.getTen());
            res.setGia(sanPham.getGia());
            res.setMota(sanPham.getMota());
            res.setHinhAnh(sanPham.getHinhAnh());
            List<String> tenTheLoais = sanPham.getTheLoais().stream()
                    .map(TheLoai::getTen)
                    .collect(Collectors.toList());
            res.setTenTheLoais(tenTheLoais);
            return res;
        }).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm id: " + id));

        ApiResponse<SanPhamResponse> res = new ApiResponse<>(200, "Thành công", sp);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/phantrang")
    public PageResponse<SanPham> getSanPhamPhanTrang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SanPham> pageSanPham = repo.findAll(pageable);

        PageResponse<SanPham> res = new PageResponse<>();
        res.setContent(pageSanPham.getContent());
        res.setPageNumber(pageSanPham.getNumber());
        res.setPageSize(pageSanPham.getSize());
        res.setTotalElements(pageSanPham.getTotalElements());
        res.setTotalPages(pageSanPham.getTotalPages());
        res.setLast(pageSanPham.isLast());

        return res;
    }

    @GetMapping("/search")
    public PageResponse<SanPham> searchSanPham(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SanPham> pageSanPham = repo.findByTenContainingIgnoreCase(keyword, pageable);

        PageResponse<SanPham> res = new PageResponse<>();
        res.setContent(pageSanPham.getContent());
        res.setPageNumber(pageSanPham.getNumber());
        res.setPageSize(pageSanPham.getSize());
        res.setTotalElements(pageSanPham.getTotalElements());
        res.setTotalPages(pageSanPham.getTotalPages());
        res.setLast(pageSanPham.isLast());

        return res;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SanPhamResponse>> create(@RequestBody SanPhamRequest req) {
        SanPham sp = new SanPham();
        sp.setTen(req.getTen());
        sp.setGia(req.getGia());
        sp.setMota(req.getMota());
        sp.setHinhAnh(req.getHinhAnh());
        List<TheLoai> theLoais = theLoaiRepo.findAllById(req.getTheLoaiIds());
        sp.setTheLoais(theLoais);

        SanPham saved = repo.save(sp);

        // Convert to SanPhamResponse
        SanPhamResponse response = new SanPhamResponse();
        response.setId(saved.getId());
        response.setTen(saved.getTen());
        response.setGia(saved.getGia());
        response.setMota(saved.getMota());
        response.setHinhAnh(saved.getHinhAnh());
        List<String> tenTheLoais = saved.getTheLoais().stream()
                .map(TheLoai::getTen)
                .collect(Collectors.toList());
        response.setTenTheLoais(tenTheLoais);

        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo sản phẩm thành công", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SanPhamResponse>> update(@PathVariable Long id,
            @RequestBody SanPhamRequest newData) {
        SanPham sp = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm id: " + id));

        sp.setTen(newData.getTen());
        sp.setGia(newData.getGia());
        sp.setMota(newData.getMota());
        sp.setHinhAnh(newData.getHinhAnh());
        List<TheLoai> theLoais = theLoaiRepo.findAllById(newData.getTheLoaiIds());
        sp.setTheLoais(theLoais);

        SanPham updated = repo.save(sp);

        // Convert to SanPhamResponse
        SanPhamResponse response = new SanPhamResponse();
        response.setId(updated.getId());
        response.setTen(updated.getTen());
        response.setGia(updated.getGia());
        response.setMota(updated.getMota());
        response.setHinhAnh(updated.getHinhAnh());
        List<String> tenTheLoais = updated.getTheLoais().stream()
                .map(TheLoai::getTen)
                .collect(Collectors.toList());
        response.setTenTheLoais(tenTheLoais);

        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật sản phẩm thành công", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
