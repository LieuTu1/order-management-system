package com.example.ordermanagementsystem.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String refreshToken;

    private String type = "Bearer";
}