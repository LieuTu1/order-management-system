package com.example.ordermanagementsystem.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
}
