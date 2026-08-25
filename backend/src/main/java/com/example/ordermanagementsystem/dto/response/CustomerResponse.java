package com.example.ordermanagementsystem.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String status;
}