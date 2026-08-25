package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.ProductRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.ProductResponse;
import com.example.ordermanagementsystem.service.FileStorageService;
import com.example.ordermanagementsystem.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(ProductController.class);

    public ProductController(
            ProductService productService,
            FileStorageService fileStorageService) {

        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<ProductResponse> productPage =
                productService.getAllProducts(pageable);

        // Ghi log khi lấy danh sách thành công
        log.info("Get all products successfully, page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Products retrieved successfully");
        response.setData(productPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(
            @PathVariable Long id) {

        ProductResponse productResponse =
                productService.getProductById(id);

        // Ghi log ID product vừa lấy
        log.info("Get product successfully, id={}", id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Product retrieved successfully");
        response.setData(productResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse productResponse =
                productService.createProduct(request);

        // Ghi log khi tạo product thành công
        log.info("Product created successfully");

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Product created successfully");
        response.setData(productResponse);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse productResponse =
                productService.updateProduct(id, request);

        // Ghi log khi sửa product thành công
        log.info("Product updated successfully, id={}", id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Product updated successfully");
        response.setData(productResponse);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        // Ghi log khi xóa product thành công
        log.info("Product deleted successfully, id={}", id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Product deleted successfully");
        response.setData(null);

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    public ResponseEntity<ApiResponse> uploadProductImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        try {

            String imageUrl =
                    fileStorageService.saveFile(file);

            ProductResponse productResponse =
                    productService.updateProductImage(id, imageUrl);

            // Ghi log khi upload thành công
            log.info("Product image uploaded successfully, id={}", id);

            ApiResponse response = new ApiResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Upload ảnh thành công");
            response.setData(productResponse);

            return ResponseEntity.ok(response);

        } catch (IOException e) {

            // Ghi log khi upload bị lỗi
            log.error("Failed to upload product image, id={}", id, e);

            ApiResponse response = new ApiResponse();
            response.setStatus(
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            response.setMessage("Upload ảnh thất bại");
            response.setData(null);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}