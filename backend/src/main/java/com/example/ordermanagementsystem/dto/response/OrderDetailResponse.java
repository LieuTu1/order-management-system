package com.example.ordermanagementsystem.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailResponse {

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}