package com.example.ordermanagementsystem.exception;

public class SupplierAlreadyExistsException extends RuntimeException {
     public SupplierAlreadyExistsException(String name) {
        super("Supplier already exists: " + name);
    }
}
