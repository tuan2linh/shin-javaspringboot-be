package com.example.demojpa.controller;

import com.example.demojpa.dto.DonHangRequest;
import com.example.demojpa.dto.DonHangResponse;
import com.example.demojpa.entity.DonHang;
import com.example.demojpa.entity.KhachHang;
import com.example.demojpa.repository.DonHangRepository;
import com.example.demojpa.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/donhang")
public class DonHangController {

    @Autowired
    private DonHangRepository donHangRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;

    @PostMapping("/them/{idKhach}")
    public DonHangResponse taoDonHang(@PathVariable Long idKhach, @RequestBody DonHangRequest request) {
        KhachHang kh = khachHangRepo.findById(idKhach).orElseThrow();

        DonHang donHang = new DonHang();
        donHang.setNgayDat(LocalDate.now());
        donHang.setTongTien(request.getTongTien());
        donHang.setKhachHang(kh);

        DonHang saved = donHangRepo.save(donHang);

        DonHangResponse res = new DonHangResponse();
        res.setId(saved.getId());
        res.setNgayDat(saved.getNgayDat());
        res.setTongTien(saved.getTongTien());
        res.setTenKhach(kh.getHoTen());

        return res;
    }

    @GetMapping
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
}
