package com.example.ordermanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor

public class CategoryRequest {

    @NotBlank(message = "Category name must not be blank")
    private String name;

    private String description;

}
