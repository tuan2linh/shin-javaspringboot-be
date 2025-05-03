package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.KhachHangRequest;
import com.example.demojpa.dto.KhachHangResponse;
import com.example.demojpa.dto.DonHangResponse;
import com.example.demojpa.entity.KhachHang;
import com.example.demojpa.entity.User;
import com.example.demojpa.repository.KhachHangRepository;
import com.example.demojpa.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private UserRepository userRepo;

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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<KhachHangResponse>> getById(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return khachHangRepo.findById(id)
                .map(kh -> {
                    // Check if customer belongs to current user
                    if (!kh.getUser().getId().equals(currentUser.getId())) {
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

    @GetMapping("/user")
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KhachHang> create(@Valid @RequestBody KhachHangRequest req) {

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<KhachHang>> update(
            @PathVariable Long id,
            @Valid @RequestBody KhachHangRequest req) {
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

                    KhachHang updatedKh = khachHangRepo.save(kh);
                    ApiResponse<KhachHang> response = new ApiResponse<>(200,
                            "Cập nhật thông tin khách hàng thành công", updatedKh);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(404,
                                "Không tìm thấy khách hàng với ID: " + id, null)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
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
