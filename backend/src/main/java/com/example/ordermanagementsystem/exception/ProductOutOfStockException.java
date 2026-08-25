package com.example.ordermanagementsystem.exception;

public class ProductOutOfStockException extends RuntimeException {

    public ProductOutOfStockException(String productName) {
        super("Sản phẩm '" + productName + "' không đủ hàng trong kho");
    }
}