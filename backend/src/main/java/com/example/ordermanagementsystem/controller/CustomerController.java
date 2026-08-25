package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.CustomerRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.CustomerResponse;
import com.example.ordermanagementsystem.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // 1. GET all
    // Lấy danh sách tất cả Customer (có phân trang)
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCustomers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<CustomerResponse> customerPage =
                customerService.getAllCustomers(pageable);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Customer retrieved successfully");
        response.setData(customerPage);

        log.info("Get all customers successfully");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 2. GET by id
    // Lấy Customer theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long id) {

        CustomerResponse customerResponse =
                customerService.getCustomerById(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setData(customerResponse);

        log.info("Get customer successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 3. POST create
    // Tạo Customer mới
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse customerResponse =
                customerService.createCustomer(request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Customer created successfully");
        response.setData(customerResponse);

        log.info("Customer created successfully, id={}",
                customerResponse.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. PUT update
    // Cập nhật Customer
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse customerResponse =
                customerService.updateCustomer(id, request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Customer updated successfully");
        response.setData(customerResponse);

        log.info("Customer updated successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 5. DELETE
    // Xóa Customer theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Customer deleted successfully");
        response.setData(null);

        log.info("Customer deleted successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}