package com.example.ordermanagementsystem.dto.response;

import com.example.ordermanagementsystem.enums.OrderStatus;
import com.example.ordermanagementsystem.enums.PaymentMethod;
import com.example.ordermanagementsystem.enums.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderCode;
    private LocalDateTime orderDate;
    private Long customerId;
    private String customerName;
    private Long userId;
    private String userName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private List<OrderDetailResponse> orderDetails = new ArrayList<>();
}