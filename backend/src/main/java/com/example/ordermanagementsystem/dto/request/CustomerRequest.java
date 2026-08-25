package com.example.ordermanagementsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String name;

    private String phone;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String address;
}