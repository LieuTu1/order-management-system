package com.example.ordermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class SupplierResponse {

    private Long id;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String status;
}