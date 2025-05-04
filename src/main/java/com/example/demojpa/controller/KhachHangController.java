package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.KhachHangRequest;
import com.example.demojpa.dto.KhachHangResponse;
import com.example.demojpa.dto.DonHangResponse;
import com.example.demojpa.entity.KhachHang;
import com.example.demojpa.entity.User;
import com.example.demojpa.entity.Role;
import com.example.demojpa.repository.KhachHangRepository;
import com.example.demojpa.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/khachhang")
@Tag(name = "06. Khách hàng", description = "Quản lý khách hàng APIs - Mỗi user có thể có nhiều khách hàng")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private UserRepository userRepo;

    @Operation(summary = "Lấy tất cả khách hàng", description = "API này trả về danh sách tất cả khách hàng (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<KhachHangResponse>>> getAll() {
        List<KhachHang> khachHangs = khachHangRepo.findAll();
        List<KhachHangResponse> responses = khachHangs.stream()
                .map(kh -> {
                    KhachHangResponse res = new KhachHangResponse();
                    res.setId(kh.getId());
                    res.setHoTen(kh.getHoTen());
                    res.setEmail(kh.getEmail());
                    res.setSoDienThoai(kh.getSoDienThoai());
                    res.setDiaChi(kh.getDiaChi());

                    // Map don hang
                    if (kh.getDonHangs() != null) {
                        List<DonHangResponse> donHangResponses = kh.getDonHangs().stream()
                                .map(dh -> {
                                    DonHangResponse dhRes = new DonHangResponse();
                                    dhRes.setId(dh.getId());
                                    dhRes.setNgayDat(dh.getNgayDat());
                                    dhRes.setTongTien(dh.getTongTien());
                                    dhRes.setTenKhach(kh.getHoTen());
                                    return dhRes;
                                }).collect(Collectors.toList());
                        res.setDonHangs(donHangResponses);
                    }
                    return res;
                }).collect(Collectors.toList());

        ApiResponse<List<KhachHangResponse>> response = new ApiResponse<>(200,
                "Lấy danh sách khách hàng thành công", responses);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy khách hàng theo ID", description = "API này trả về thông tin chi tiết của một khách hàng. ADMIN có thể xem tất cả, USER chỉ có thể xem khách hàng của mình", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<KhachHangResponse>> getById(
            @Parameter(description = "ID của khách hàng") @PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        return khachHangRepo.findById(id)
                .map(kh -> {
                    // Skip ownership check if user is admin
                    if (!isAdmin && !kh.getUser().getId().equals(currentUser.getId())) {
                        ApiResponse<KhachHangResponse> response = new ApiResponse<>(403,
                                "Bạn không có quyền xem thông tin khách hàng này", null);
                        return ResponseEntity.status(403).body(response);
                    }

                    // Convert to DTO
                    KhachHangResponse res = new KhachHangResponse();
                    res.setId(kh.getId());
                    res.setHoTen(kh.getHoTen());
                    res.setEmail(kh.getEmail());
                    res.setSoDienThoai(kh.getSoDienThoai());
                    res.setDiaChi(kh.getDiaChi());

                    // Map don hang
                    if (kh.getDonHangs() != null) {
                        List<DonHangResponse> donHangResponses = kh.getDonHangs().stream()
                                .map(dh -> {
                                    DonHangResponse dhRes = new DonHangResponse();
                                    dhRes.setId(dh.getId());
                                    dhRes.setNgayDat(dh.getNgayDat());
                                    dhRes.setTongTien(dh.getTongTien());
                                    dhRes.setTenKhach(kh.getHoTen());
                                    return dhRes;
                                }).collect(Collectors.toList());
                        res.setDonHangs(donHangResponses);
                    }

                    ApiResponse<KhachHangResponse> response = new ApiResponse<>(200,
                            "Lấy thông tin khách hàng thành công", res);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(404,
                                "Không tìm thấy khách hàng với ID: " + id, null)));
    }

    @Operation(summary = "Lấy danh sách khách hàng của user hiện tại", description = "API này trả về danh sách khách hàng của user đang đăng nhập (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<KhachHangResponse>>> getMyCustomers() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<KhachHang> khachHangs = khachHangRepo.findAllByUserId(currentUser.getId());

        List<KhachHangResponse> responses = khachHangs.stream()
                .map(kh -> {
                    KhachHangResponse res = new KhachHangResponse();
                    res.setId(kh.getId());
                    res.setHoTen(kh.getHoTen());
                    res.setEmail(kh.getEmail());
                    res.setSoDienThoai(kh.getSoDienThoai());
                    res.setDiaChi(kh.getDiaChi());

                    // Map don hang
                    if (kh.getDonHangs() != null) {
                        List<DonHangResponse> donHangResponses = kh.getDonHangs().stream()
                                .map(dh -> {
                                    DonHangResponse dhRes = new DonHangResponse();
                                    dhRes.setId(dh.getId());
                                    dhRes.setNgayDat(dh.getNgayDat());
                                    dhRes.setTongTien(dh.getTongTien());
                                    dhRes.setTenKhach(kh.getHoTen());
                                    return dhRes;
                                }).collect(Collectors.toList());
                        res.setDonHangs(donHangResponses);
                    }
                    return res;
                }).collect(Collectors.toList());

        ApiResponse<List<KhachHangResponse>> response = new ApiResponse<>(200,
                "Lấy danh sách khách hàng của bạn thành công", responses);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tạo khách hàng mới", description = "API này tạo một khách hàng mới và gán cho user hiện tại (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<KhachHang> create(
            @Parameter(description = "Thông tin khách hàng") @Valid @RequestBody KhachHangRequest req) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User copyUser = currentUser;
        KhachHang kh = new KhachHang();
        kh.setHoTen(req.getHoTen());
        kh.setEmail(req.getEmail());
        kh.setSoDienThoai(req.getSoDienThoai());
        kh.setDiaChi(req.getDiaChi());
        copyUser.setPassword(null); // Để không trả về mật khẩu
        kh.setUser(copyUser);

        return ResponseEntity.ok(khachHangRepo.save(kh));
    }

    @Operation(summary = "Cập nhật thông tin khách hàng", description = "API này cập nhật thông tin của một khách hàng. USER chỉ có thể cập nhật khách hàng của mình", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<KhachHang>> update(
            @Parameter(description = "ID của khách hàng") @PathVariable Long id,
            @Parameter(description = "Thông tin cập nhật") @Valid @RequestBody KhachHangRequest req) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return khachHangRepo.findById(id)
                .map(kh -> {
                    // Check if customer belongs to current user
                    if (!kh.getUser().getId().equals(currentUser.getId())) {
                        ApiResponse<KhachHang> response = new ApiResponse<>(403,
                                "Bạn không có quyền cập nhật thông tin khách hàng này", null);
                        return ResponseEntity.status(403).body(response);
                    }

                    // Update customer data if authorized
                    kh.setHoTen(req.getHoTen());
                    kh.setEmail(req.getEmail());
                    kh.setSoDienThoai(req.getSoDienThoai());
                    kh.setDiaChi(req.getDiaChi());
                    // ẩn đi mật khẩu
                    User copyUser = currentUser;
                    copyUser.setPassword(null);
                    kh.setUser(copyUser);
                    KhachHang updatedKh = khachHangRepo.save(kh);
                    ApiResponse<KhachHang> response = new ApiResponse<>(200,
                            "Cập nhật thông tin khách hàng thành công", updatedKh);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(404,
                                "Không tìm thấy khách hàng với ID: " + id, null)));
    }

    @Operation(summary = "Xóa khách hàng", description = "API này xóa một khách hàng. USER chỉ có thể xóa khách hàng của mình", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID của khách hàng") @PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return khachHangRepo.findById(id)
                .map(kh -> {
                    if (!kh.getUser().getId().equals(currentUser.getId())) {
                        ApiResponse<Void> response = new ApiResponse<>(403,
                                "Bạn không có quyền xóa khách hàng này", null);
                        return ResponseEntity.status(403).body(response);
                    }

                    khachHangRepo.delete(kh);
                    ApiResponse<Void> response = new ApiResponse<>(200,
                            "Xóa khách hàng thành công", null);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(404,
                                "Không tìm thấy khách hàng với ID: " + id, null)));
    }
}
