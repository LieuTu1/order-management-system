package com.example.ordermanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "SKU must not be blank")
    private String sku;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Stock must be greater than or equal to 0")
    private Integer stock;

    private String imageUrl;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    @NotBlank(message = "Status must not be blank")
    private String status;
}