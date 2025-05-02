package com.example.demojpa.controller;

import com.example.demojpa.dto.TheLoaiResponse;
import com.example.demojpa.entity.TheLoai;
import com.example.demojpa.repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/theloai")
public class TheLoaiController {

    @Autowired
    private TheLoaiRepository theLoaiRepo;

    // GET /theloai
    @GetMapping
    public List<TheLoaiResponse> getAll() {
        return theLoaiRepo.findAll().stream().map(tl -> {
            TheLoaiResponse res = new TheLoaiResponse();
            res.setId(tl.getId());
            res.setTen(tl.getTen());
            res.setSoLuongSanPham(
                tl.getSanPhams() != null ? tl.getSanPhams().size() : 0
            );
            return res;
        }).collect(Collectors.toList());
    }

    // GET /theloai/{id}
    @GetMapping("/{id}")
    public TheLoai getById(@PathVariable Long id) {
        return theLoaiRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
    }

    // POST /theloai
    @PostMapping
    public TheLoai create(@RequestBody TheLoai theLoai) {
        return theLoaiRepo.save(theLoai);
    }

    // PUT /theloai/{id}
    @PutMapping("/{id}")
    public TheLoai update(@PathVariable Long id, @RequestBody TheLoai data) {
        TheLoai old = theLoaiRepo.findById(id).orElseThrow();
        old.setTen(data.getTen());
        return theLoaiRepo.save(old);
    }

    // DELETE /theloai/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        theLoaiRepo.deleteById(id);
    }
}
