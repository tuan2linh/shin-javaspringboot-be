package com.example.demojpa.controller;

import com.example.demojpa.dto.ApiResponse;
import com.example.demojpa.dto.AuthRequest;
import com.example.demojpa.dto.AuthResponse;
import com.example.demojpa.dto.RefreshTokenRequest;
import com.example.demojpa.entity.RefreshToken;
import com.example.demojpa.entity.Role;
import com.example.demojpa.entity.User;
import com.example.demojpa.repository.UserRepository;
import com.example.demojpa.service.CartService;
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

    @Autowired
    private CartService cartService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody AuthRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Username đã tồn tại", null));
        }

        // Tạo user mới
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        user = userRepo.save(user);

        // Tạo cart cho user mới
        cartService.getOrCreateCart(user);

        // Tạo token
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user);
        String refreshToken = refreshTokenEntity.getToken();

        // Tạo response
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, user.getRole());

        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng ký thành công", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            User user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Sai username hoặc password"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Sai username hoặc password");
            }

            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user);
            String refreshToken = refreshTokenEntity.getToken();

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, user.getRole());

            return ResponseEntity.ok(new ApiResponse<>(200, "Đăng nhập thành công", authResponse));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword("********");
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin profile thành công", user));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

            refreshTokenService.verifyExpiration(refreshToken);

            User user = refreshToken.getUser();

            String accessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = refreshTokenService.createRefreshToken(user).getToken();

            AuthResponse authResponse = new AuthResponse(accessToken, newRefreshToken, user.getRole());

            return ResponseEntity.ok(new ApiResponse<>(200, "Refresh token thành công", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                tokenBlacklistService.blacklistToken(token);
            }

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            refreshTokenService.deleteByUser(user);

            return ResponseEntity.ok(new ApiResponse<>(200, "Đăng xuất thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Đăng xuất thất bại", null));
        }
    }

}
