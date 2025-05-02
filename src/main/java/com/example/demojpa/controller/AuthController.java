package com.example.demojpa.controller;

import com.example.demojpa.dto.AuthRequest;
import com.example.demojpa.dto.AuthResponse;
import com.example.demojpa.dto.RefreshTokenRequest;
import com.example.demojpa.entity.RefreshToken;
import com.example.demojpa.entity.Role;
import com.example.demojpa.entity.User;
import com.example.demojpa.repository.UserRepository;
import com.example.demojpa.service.JwtService;
import com.example.demojpa.service.RefreshTokenService;
import com.example.demojpa.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // mặc định đăng ký là user
                .build();
        userRepo.save(user);
        return "Đăng ký thành công!";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Sai username hoặc password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai username hoặc password");
        }

        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user);
        String refreshToken = refreshTokenEntity.getToken(); // 🔥 lấy token đã lưu vào DB!

        return new AuthResponse(accessToken, refreshToken);
}
    // @GetMapping("/profile")
    // public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String bearerToken) {
    // // bearerToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    //     String token = bearerToken.substring(7); // bỏ chữ "Bearer "
    //     String username = jwtService.extractUsername(token);

    //     User user = userRepo.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

    //     // Không trả password
    //     user.setPassword("********");

    //     return ResponseEntity.ok(user);
    // }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword("********");
        return ResponseEntity.ok(user);
    }
    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ."));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return new AuthResponse(accessToken, newRefreshToken);
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token); // 🔥 Blacklist token
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.deleteByUser(user); // xóa refresh token
        return "Đăng xuất thành công!";
    }

}
