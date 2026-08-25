package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.ProductRequest;
import com.example.ordermanagementsystem.dto.response.ProductResponse;
import com.example.ordermanagementsystem.entity.Category;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.entity.Supplier;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.CategoryRepository;
import com.example.ordermanagementsystem.repository.ProductRepository;
import com.example.ordermanagementsystem.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    // Logger của ProductService
    private static final Logger log =
            LoggerFactory.getLogger(ProductService.class);

    public ProductService(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
    }

    // Chuyển Product entity thành ProductResponse để trả về API
    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        response.setSupplierId(product.getSupplier().getId());
        response.setSupplierName(product.getSupplier().getName());

        return response;
    }

    // Lấy tất cả Product từ database
    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        List<ProductResponse> responses = new ArrayList<>();

        for (Product product : products) {
            responses.add(mapToResponse(product));
        }

        return responses;
    }

    // Lấy Product có phân trang
    public Page<ProductResponse> getAllProducts(Pageable pageable) {

        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(this::mapToResponse);
    }

    // Tìm Product theo ID
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", id));

        return mapToResponse(product);
    }

    // Tạo Product mới
    public ProductResponse createProduct(ProductRequest request) {

        // Tìm Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category",
                                request.getCategoryId()
                        ));

        // Tìm Supplier
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier",
                                request.getSupplierId()
                        ));

        // Tạo Product mới
        Product product = new Product();

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setStatus("ACTIVE");

        product.setCategory(category);
        product.setSupplier(supplier);

        // Lưu Product vào database
        productRepository.save(product);

        log.info("Product created, id={}", product.getId());

        return mapToResponse(product);
    }

    // Cập nhật thông tin Product
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        // Tìm Product cần sửa
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", id));

        // Tìm Category mới
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category",
                                request.getCategoryId()
                        ));

        // Tìm Supplier mới
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier",
                                request.getSupplierId()
                        ));

        // Cập nhật thông tin Product
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());
        product.setImageUrl(request.getImageUrl());

        product.setCategory(category);
        product.setSupplier(supplier);

        // Lưu thay đổi
        productRepository.save(product);

        log.info("Product updated, id={}", id);

        return mapToResponse(product);
    }

    // Xóa Product theo ID
    public void deleteProduct(Long id) {

        // Kiểm tra Product có tồn tại không
        productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", id));

        // Xóa Product
        productRepository.deleteById(id);

        log.info("Product deleted, id={}", id);
    }

    // Cập nhật riêng ảnh của Product
    public ProductResponse updateProductImage(
            Long id,
            String imageUrl) {

        // Tìm Product
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", id));

        // Cập nhật URL ảnh
        product.setImageUrl(imageUrl);

        // Lưu thay đổi
        productRepository.save(product);

        log.info("Product image updated, id={}", id);

        return mapToResponse(product);
    }
}