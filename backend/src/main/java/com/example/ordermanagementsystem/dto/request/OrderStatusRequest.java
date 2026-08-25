package com.example.ordermanagementsystem.dto.request;

import com.example.ordermanagementsystem.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {
    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus status;
}
