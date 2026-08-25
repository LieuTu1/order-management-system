package com.example.ordermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor

public class ProductResponse {

    private Long id;

    private String sku;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private String imageUrl;

    private String status;

    private Long categoryId;

    private String categoryName;

    private Long supplierId;

    private String supplierName;
}
