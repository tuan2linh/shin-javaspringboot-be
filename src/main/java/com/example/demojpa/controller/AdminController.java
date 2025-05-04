package com.example.demojpa.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// @RestController
@RequestMapping("/admin")
public class AdminController {

    // @GetMapping("/hello")
    // @PreAuthorize("hasRole('ADMIN')") // ✨ Chỉ ADMIN mới được vào
    // public String helloAdmin() {
    //     return "Hello Admin!";
    // }
}
