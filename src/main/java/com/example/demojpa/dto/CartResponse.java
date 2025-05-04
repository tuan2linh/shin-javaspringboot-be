package com.example.demojpa.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private double totalAmount;
}