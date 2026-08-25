package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.OrderRequest;
import com.example.ordermanagementsystem.dto.request.OrderStatusRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.OrderResponse;
import com.example.ordermanagementsystem.service.OrderService;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. POST create
    // Tạo đơn hàng mới
    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {

        OrderResponse orderResponse =
                orderService.createOrder(request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Order created successfully");
        response.setData(orderResponse);

        log.info("Order created successfully, id={}",
                orderResponse.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. GET all
    // Lấy danh sách tất cả đơn hàng
    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrders(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<OrderResponse> orderPage =
                orderService.getAllOrders(pageable);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Orders retrieved successfully");
        response.setData(orderPage);

        log.info("Get all orders successfully");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 3. GET by id
    // Lấy đơn hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrderById(
            @PathVariable Long id) {

        OrderResponse orderResponse =
                orderService.getOrderById(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Order retrieved successfully");
        response.setData(orderResponse);

        log.info("Get order successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 4. PUT update status
    // Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request) {

        OrderResponse orderResponse =
                orderService.updateOrderStatus(id, request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Order status updated successfully");
        response.setData(orderResponse);

        log.info("Order status updated successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}