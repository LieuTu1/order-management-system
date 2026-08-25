package com.example.ordermanagementsystem.dto.request;

import com.example.ordermanagementsystem.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "Khách hàng không được để trống")
    private Long customerId;

    @NotNull(message = "Nhân viên không được để trống")
    private Long userId;

    @NotEmpty(message = "Đơn hàng phải có ít nhất một sản phẩm")
    @Valid
    private List<OrderDetailRequest> orderDetails = new ArrayList<>();

    private PaymentMethod paymentMethod;
}
