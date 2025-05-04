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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/sanpham")
@Tag(name = "02. Sản phẩm", description = "Quản lý sản phẩm APIs")
public class SanPhamController {

        @Autowired
        private SanPhamRepository repo;

        @Autowired
        private TheLoaiRepository theLoaiRepo;

        @Operation(summary = "Lấy danh sách sản phẩm", description = "API này trả về tất cả sản phẩm")
        @GetMapping
        public ResponseEntity<ApiResponse<List<SanPhamResponse>>> getAll() {
                List<SanPhamResponse> responses = repo.findAll().stream().map(sp -> {
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

                return ResponseEntity.ok(new ApiResponse<>(
                                HttpStatus.OK.value(),
                                "Lấy danh sách sản phẩm thành công",
                                responses));
        }

        @Operation(summary = "Lấy sản phẩm theo ID", description = "API này trả về một sản phẩm dựa trên ID")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<SanPhamResponse>> getSanPhamById(@PathVariable Long id) {
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

        @Operation(summary = "Lấy danh sách sản phẩm phân trang", description = "API này trả về danh sách sản phẩm có phân trang và sắp xếp")
        @GetMapping("/phantrang")
        public ResponseEntity<ApiResponse<PageResponse<SanPhamResponse>>> getSanPhamPhanTrang(
                        @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Số lượng item mỗi trang") @RequestParam(defaultValue = "5") int size,
                        @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "id") String sortBy,
                        @Parameter(description = "Thứ tự sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

                Pageable pageable = PageRequest.of(page, size, sort);
                Page<SanPham> pageSanPham = repo.findAll(pageable);

                List<SanPhamResponse> responses = pageSanPham.getContent().stream()
                                .map(sp -> {
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

                PageResponse<SanPhamResponse> pageResponse = new PageResponse<>();
                pageResponse.setContent(responses);
                pageResponse.setPageNumber(pageSanPham.getNumber());
                pageResponse.setPageSize(pageSanPham.getSize());
                pageResponse.setTotalElements(pageSanPham.getTotalElements());
                pageResponse.setTotalPages(pageSanPham.getTotalPages());
                pageResponse.setLast(pageSanPham.isLast());

                return ResponseEntity.ok(new ApiResponse<>(
                                HttpStatus.OK.value(),
                                "Lấy danh sách sản phẩm phân trang thành công",
                                pageResponse));
        }

        @Operation(summary = "Tìm kiếm sản phẩm", description = "API này tìm kiếm sản phẩm theo từ khóa với phân trang")
        @GetMapping("/search")
        public ResponseEntity<ApiResponse<PageResponse<SanPhamResponse>>> searchSanPham(
                        @Parameter(description = "Từ khóa tìm kiếm") @RequestParam String keyword,
                        @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Số lượng item mỗi trang") @RequestParam(defaultValue = "5") int size,
                        @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "id") String sortBy,
                        @Parameter(description = "Thứ tự sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

                Sort sort = sortDir.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

                Pageable pageable = PageRequest.of(page, size, sort);
                Page<SanPham> pageSanPham = repo.findByTenContainingIgnoreCase(keyword, pageable);

                List<SanPhamResponse> responses = pageSanPham.getContent().stream()
                                .map(sp -> {
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

                PageResponse<SanPhamResponse> pageResponse = new PageResponse<>();
                pageResponse.setContent(responses);
                pageResponse.setPageNumber(pageSanPham.getNumber());
                pageResponse.setPageSize(pageSanPham.getSize());
                pageResponse.setTotalElements(pageSanPham.getTotalElements());
                pageResponse.setTotalPages(pageSanPham.getTotalPages());
                pageResponse.setLast(pageSanPham.isLast());

                return ResponseEntity.ok(new ApiResponse<>(
                                HttpStatus.OK.value(),
                                "Tìm kiếm sản phẩm thành công",
                                pageResponse));
        }

        @Operation(summary = "Tạo sản phẩm mới", description = "API này tạo một sản phẩm mới (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<SanPhamResponse>> create(
                        @Parameter(description = "Thông tin sản phẩm") @Valid @RequestBody SanPhamRequest req) {
                // Kiểm tra tồn tại của các thể loại
                List<TheLoai> theLoais = theLoaiRepo.findAllById(req.getTheLoaiIds());
                if (theLoais.size() != req.getTheLoaiIds().size()) {
                        return ResponseEntity.badRequest()
                                        .body(new ApiResponse<>(
                                                        HttpStatus.BAD_REQUEST.value(),
                                                        "Một số thể loại không tồn tại",
                                                        null));
                }

                SanPham sp = new SanPham();
                sp.setTen(req.getTen());
                sp.setGia(req.getGia());
                sp.setMota(req.getMota());
                sp.setHinhAnh(req.getHinhAnh());
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

                return ResponseEntity.ok(new ApiResponse<>(
                                HttpStatus.CREATED.value(),
                                "Tạo sản phẩm thành công",
                                response));
        }

        @Operation(summary = "Cập nhật sản phẩm", description = "API này cập nhật thông tin sản phẩm theo ID (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<SanPhamResponse>> update(
                        @Parameter(description = "ID sản phẩm") @PathVariable Long id,
                        @Parameter(description = "Thông tin cập nhật") @RequestBody SanPhamRequest newData) {
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

        @Operation(summary = "Xóa sản phẩm", description = "API này xóa sản phẩm theo ID (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Void>> delete(
                        @Parameter(description = "ID sản phẩm") @PathVariable Long id) {
                repo.deleteById(id);
                return ResponseEntity.ok(new ApiResponse<>(
                                HttpStatus.OK.value(),
                                "Xóa sản phẩm thành công",
                                null));
        }
}
