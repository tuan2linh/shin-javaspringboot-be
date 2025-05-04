package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.DonHangRequest;
import com.example.demojpa.dto.DonHangResponse;
import com.example.demojpa.dto.DonHangUpdateRequest;
import com.example.demojpa.dto.ChiTietDonHangRequest;
import com.example.demojpa.dto.ChiTietDonHangResponse;
import com.example.demojpa.entity.*;
import com.example.demojpa.repository.*;
import com.example.demojpa.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
@Tag(name = "05. Đơn hàng", description = "Quản lý đơn hàng APIs - User muốn tạo đơn hàng cần gắn đơn hàng đó với các khách hàng thuộc về user đó")
public class DonHangController {

    @Autowired
    private DonHangRepository donHangRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private SanPhamRepository sanPhamRepo;

    @Autowired
    private ChiTietDonHangRepository chiTietRepo;

    @Autowired
    private CartService cartService;

    @Operation(summary = "Lấy tất cả đơn hàng", description = "API này trả về danh sách tất cả đơn hàng (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DonHangResponse>>> getAll() {
        List<DonHangResponse> responses = donHangRepo.findAll().stream().map(dh -> {
            DonHangResponse res = new DonHangResponse();
            res.setId(dh.getId());
            res.setNgayDat(dh.getNgayDat());
            res.setTongTien(dh.getTongTien());
            res.setTenKhach(dh.getKhachHang().getHoTen());
            res.setTrangThai(dh.getTrangThai());
            res.setHidden(dh.isHidden());

            // Map chi tiết đơn hàng
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

            return res;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "Lấy danh sách đơn hàng thành công",
                responses));
    }

    @Operation(summary = "Lấy đơn hàng theo ID", description = "API này trả về chi tiết một đơn hàng. ADMIN có thể xem tất cả, USER chỉ xem được đơn hàng của mình", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<DonHangResponse>> getById(
            @Parameter(description = "ID của đơn hàng") @PathVariable Long id) {
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

    @Operation(summary = "Tạo đơn hàng mới", description = "API này tạo một đơn hàng mới từ danh sách sản phẩm (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DonHangResponse>> taoDonHang(
            @Parameter(description = "Thông tin đơn hàng") @RequestBody DonHangRequest request) {
        try {
            // Validate request
            if (request.getChiTietDonHang() == null || request.getChiTietDonHang().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(400, "Đơn hàng phải có ít nhất một sản phẩm", null));
            }

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
            donHang.setTrangThai(TrangThaiDonHang.PENDING);

            double tongTien = 0;

            for (ChiTietDonHangRequest ctReq : request.getChiTietDonHang()) {
                SanPham sanPham = sanPhamRepo.findById(ctReq.getSanPhamId())
                        .orElseThrow(
                                () -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + ctReq.getSanPhamId()));

                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(donHang);
                chiTiet.setSanPham(sanPham);
                chiTiet.setSoLuong(ctReq.getSoLuong());
                chiTiet.setDonGia(sanPham.getGia());

                tongTien += chiTiet.getDonGia() * ctReq.getSoLuong();
                donHang.getChiTietDonHangList().add(chiTiet);
            }

            donHang.setTongTien(tongTien);
            DonHang saved = donHangRepo.save(donHang);

            // Create response with full order details
            DonHangResponse res = new DonHangResponse();
            res.setId(saved.getId());
            res.setNgayDat(saved.getNgayDat());
            res.setTongTien(saved.getTongTien());
            res.setTenKhach(khachHang.getHoTen());
            res.setTrangThai(saved.getTrangThai());
            res.setHidden(saved.isHidden());

            // Map chi tiết đơn hàng
            List<ChiTietDonHangResponse> chiTietList = saved.getChiTietDonHangList()
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

            return ResponseEntity.ok(new ApiResponse<>(200, "Tạo đơn hàng thành công", res));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @Operation(summary = "Tạo đơn hàng từ giỏ hàng", description = "API này tạo đơn hàng từ giỏ hàng hiện tại của user (Chỉ USER)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DonHangResponse>> createFromCart(
            @Parameter(description = "ID của khách hàng") @RequestParam Long khachHangId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Find customer
            KhachHang khachHang = khachHangRepo.findById(khachHangId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

            // Verify customer belongs to current user
            if (!khachHang.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse<>(403, "Bạn không có quyền tạo đơn hàng cho khách hàng này", null));
            }

            // Get cart
            Cart cart = cartService.getOrCreateCart(currentUser);
            if (cart.getCartItems().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(400, "Giỏ hàng trống", null));
            }

            // Create order
            DonHang donHang = new DonHang();
            donHang.setNgayDat(LocalDate.now());
            donHang.setKhachHang(khachHang);
            donHang.setChiTietDonHangList(new ArrayList<>());
            donHang.setTrangThai(TrangThaiDonHang.PENDING);

            double tongTien = 0;

            // Convert cart items to order details
            for (CartItem cartItem : cart.getCartItems()) {
                ChiTietDonHang chiTiet = new ChiTietDonHang();
                chiTiet.setDonHang(donHang);
                chiTiet.setSanPham(cartItem.getSanPham());
                chiTiet.setSoLuong(cartItem.getSoLuong());
                chiTiet.setDonGia(cartItem.getSanPham().getGia());

                tongTien += chiTiet.getDonGia() * cartItem.getSoLuong();
                donHang.getChiTietDonHangList().add(chiTiet);
            }

            donHang.setTongTien(tongTien);
            DonHang saved = donHangRepo.save(donHang);
            cartService.clearCart(currentUser);

            // Create response with full order details
            DonHangResponse res = new DonHangResponse();
            res.setId(saved.getId());
            res.setNgayDat(saved.getNgayDat());
            res.setTongTien(saved.getTongTien());
            res.setTenKhach(khachHang.getHoTen());
            res.setTrangThai(saved.getTrangThai());
            res.setHidden(saved.isHidden());

            // Map order details
            List<ChiTietDonHangResponse> chiTietList = saved.getChiTietDonHangList()
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

            return ResponseEntity.ok(new ApiResponse<>(200, "Tạo đơn hàng thành công", res));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @Operation(summary = "Cập nhật đơn hàng", description = "API này cập nhật thông tin đơn hàng. ADMIN có thể cập nhật tất cả thông tin, USER chỉ có thể ẩn/hiện đơn hàng của mình", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<DonHangResponse>> updateDonHang(
            @Parameter(description = "ID của đơn hàng") @PathVariable Long id,
            @Parameter(description = "Thông tin cập nhật") @RequestBody DonHangUpdateRequest request) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        DonHang donHang = donHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền
        if (!isAdmin && !donHang.getKhachHang().getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(403, "Bạn không có quyền cập nhật đơn hàng này", null));
        }

        // Chỉ ADMIN mới được thay đổi trạng thái
        if (request.getTrangThai() != null) {
            if (!isAdmin) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse<>(403, "Chỉ admin mới được thay đổi trạng thái đơn hàng", null));
            }
            donHang.setTrangThai(request.getTrangThai());
        }

        // Cập nhật isHidden
        if (request.getIsHidden() != null) {
            donHang.setHidden(request.getIsHidden());
        }

        donHang = donHangRepo.save(donHang);

        // Tạo response
        DonHangResponse res = new DonHangResponse();
        res.setId(donHang.getId());
        res.setNgayDat(donHang.getNgayDat());
        res.setTongTien(donHang.getTongTien());
        res.setTenKhach(donHang.getKhachHang().getHoTen());
        res.setTrangThai(donHang.getTrangThai());
        res.setHidden(donHang.isHidden());

        // Map chi tiết đơn hàng
        List<ChiTietDonHangResponse> chiTietList = donHang.getChiTietDonHangList()
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

        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật đơn hàng thành công", res));
    }

    @Operation(summary = "Xóa đơn hàng", description = "API này xóa một đơn hàng khỏi hệ thống (Chỉ ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDonHang(
            @Parameter(description = "ID của đơn hàng") @PathVariable Long id) {
        DonHang donHang = donHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Xóa đơn hàng khỏi database
        donHangRepo.delete(donHang);

        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa đơn hàng thành công", null));
    }

}
