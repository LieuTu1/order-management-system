package com.example.ordermanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {

    @NotBlank(message = "Tên vai trò không được để trống")
    private String role;
}