package com.example.ordermanagementsystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
}
