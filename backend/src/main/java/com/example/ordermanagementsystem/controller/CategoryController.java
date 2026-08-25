package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.CategoryRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.CategoryResponse;
import com.example.ordermanagementsystem.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 1. GET all
    // Lấy danh sách tất cả Category
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {

        List<CategoryResponse> categoryResponses =
                categoryService.getAllCategories();

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setData(categoryResponses);

        log.info("Get all categories successfully");

        return ResponseEntity.ok(response);
    }

    // 2. GET by id
    // Lấy Category theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(
            @PathVariable Long id) {

        CategoryResponse categoryResponse =
                categoryService.getCategoryById(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setData(categoryResponse);

        log.info("Get category successfully, id={}", id);

        return ResponseEntity.ok(response);
    }

    // 3. POST create
    // Tạo Category mới
    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse categoryResponse =
                categoryService.createCategory(request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Category created successfully");
        response.setData(categoryResponse);

        log.info("Category created successfully, id={}",
                categoryResponse.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. PUT update
    // Cập nhật Category
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse categoryResponse =
                categoryService.updateCategory(id, request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Category updated successfully");
        response.setData(categoryResponse);

        log.info("Category updated successfully, id={}", id);

        return ResponseEntity.ok(response);
    }

    // 5. DELETE
    // Xóa Category theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Category deleted successfully");
        response.setData(null);

        log.info("Category deleted successfully, id={}", id);

        return ResponseEntity.ok(response);
    }
}