package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.SupplierRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.SupplierResponse;
import com.example.ordermanagementsystem.service.SupplierService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    final SupplierService supplierService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(SupplierController.class);

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // 1. GET all
    // Lấy danh sách tất cả Supplier
    @GetMapping
    public ResponseEntity<ApiResponse> getAllSupplier() {

        List<SupplierResponse> supplierResponses =
                supplierService.getAllSuppliers();

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Supplier retrieved successfully");
        response.setData(supplierResponses);

        log.info("Get all suppliers successfully");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 2. GET by id
    // Lấy Supplier theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long id) {

        SupplierResponse supplierResponse =
                supplierService.getSupplierById(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setData(supplierResponse);

        log.info("Get supplier successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 3. POST create
    // Tạo Supplier mới
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody SupplierRequest request) {

        SupplierResponse supplierResponse =
                supplierService.createSupplier(request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Supplier created successfully");
        response.setData(supplierResponse);

        log.info("Supplier created successfully, id={}",
                supplierResponse.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. PUT update
    // Cập nhật Supplier
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {

        SupplierResponse supplierResponse =
                supplierService.updateSupplier(id, request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Supplier updated successfully");
        response.setData(supplierResponse);

        log.info("Supplier updated successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 5. DELETE
    // Xóa Supplier theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id) {

        supplierService.deleteSupplier(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Supplier deleted successfully");
        response.setData(null);

        log.info("Supplier deleted successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}