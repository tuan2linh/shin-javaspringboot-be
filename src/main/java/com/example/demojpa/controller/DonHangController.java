package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.DonHangRequest;
import com.example.demojpa.dto.DonHangResponse;
import com.example.demojpa.dto.ChiTietDonHangRequest;
import com.example.demojpa.dto.ChiTietDonHangResponse;
import com.example.demojpa.entity.*;
import com.example.demojpa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/donhang")
public class DonHangController {

    @Autowired
    private DonHangRepository donHangRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private SanPhamRepository sanPhamRepo;

    @Autowired
    private ChiTietDonHangRepository chiTietRepo;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DonHangResponse>> taoDonHang(@RequestBody DonHangRequest request) {
        // Get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Find customer
        KhachHang khachHang = khachHangRepo.findById(request.getKhachHangId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Verify customer belongs to current user
        if (!khachHang.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(403, "Bạn không có quyền tạo đơn hàng cho khách hàng này", null));
        }

        // Create order
        DonHang donHang = new DonHang();
        donHang.setNgayDat(LocalDate.now());
        donHang.setKhachHang(khachHang);
        donHang.setChiTietDonHangList(new ArrayList<>());

        // Calculate total and add order details
        double tongTien = 0;

        for (ChiTietDonHangRequest ctReq : request.getChiTiet()) {
            SanPham sanPham = sanPhamRepo.findById(ctReq.getSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setDonHang(donHang);
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(ctReq.getSoLuong());
            chiTiet.setDonGia(sanPham.getGia()); // Save current price

            tongTien += chiTiet.getDonGia() * ctReq.getSoLuong();
            donHang.getChiTietDonHangList().add(chiTiet);
        }

        donHang.setTongTien(tongTien);
        DonHang saved = donHangRepo.save(donHang);

        // Create response
        DonHangResponse res = new DonHangResponse();
        res.setId(saved.getId());
        res.setNgayDat(saved.getNgayDat());
        res.setTongTien(saved.getTongTien());
        res.setTenKhach(khachHang.getHoTen());

        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo đơn hàng thành công", res));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DonHangResponse> getAll() {
        return donHangRepo.findAll().stream().map(dh -> {
            DonHangResponse res = new DonHangResponse();
            res.setId(dh.getId());
            res.setNgayDat(dh.getNgayDat());
            res.setTongTien(dh.getTongTien());
            res.setTenKhach(dh.getKhachHang().getHoTen());
            return res;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<DonHangResponse>> getById(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        return donHangRepo.findById(id)
                .map(dh -> {
                    // Skip ownership check for admin
                    if (!isAdmin && !dh.getKhachHang().getUser().getId().equals(currentUser.getId())) {
                        ApiResponse<DonHangResponse> response = new ApiResponse<>(403,
                                "Bạn không có quyền xem thông tin đơn hàng này", null);
                        return ResponseEntity.status(403).body(response);
                    }

                    DonHangResponse res = new DonHangResponse();
                    res.setId(dh.getId());
                    res.setNgayDat(dh.getNgayDat());
                    res.setTongTien(dh.getTongTien());
                    res.setTenKhach(dh.getKhachHang().getHoTen());

                    List<ChiTietDonHangResponse> chiTietList = dh.getChiTietDonHangList()
                            .stream()
                            .map(ct -> {
                                ChiTietDonHangResponse ctRes = new ChiTietDonHangResponse();
                                ctRes.setId(ct.getId());
                                ctRes.setTenSanPham(ct.getSanPham().getTen());
                                ctRes.setDonGia(ct.getDonGia());
                                ctRes.setSoLuong(ct.getSoLuong());
                                ctRes.setThanhTien(ct.getDonGia() * ct.getSoLuong());
                                return ctRes;
                            })
                            .collect(Collectors.toList());

                    res.setChiTietDonHang(chiTietList);

                    ApiResponse<DonHangResponse> response = new ApiResponse<>(200,
                            "Lấy thông tin đơn hàng thành công", res);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse<>(404,
                                "Không tìm thấy đơn hàng với ID: " + id, null)));
    }
}
