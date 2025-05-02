package com.example.demojpa.service;

import com.example.demojpa.entity.RefreshToken;
import com.example.demojpa.entity.User;
import com.example.demojpa.repository.RefreshTokenRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    // 
    @PersistenceContext
    private EntityManager entityManager;

    public RefreshToken createRefreshToken(User user) {
        // 🔥 Nếu đã có token cũ, thì xóa
        refreshTokenRepo.deleteByUser(user);
        entityManager.flush(); // ✨ bắt Hibernate flush ngay xuống DB
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(604800)) // 7 ngày
                .build();
        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng login lại.");
        }
        return token;
    }

    public void deleteByUser(User user) {
        refreshTokenRepo.deleteByUser(user);
    }

    // ✨ Đây là hàm em cần thêm
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }
}
