package com.example.ordermanagementsystem.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String status;

    private Long roleId;
    private String roleName;
}