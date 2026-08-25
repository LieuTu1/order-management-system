package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.CategoryRequest;
import com.example.ordermanagementsystem.dto.response.CategoryResponse;
import com.example.ordermanagementsystem.entity.Category;
import com.example.ordermanagementsystem.exception.CategoryAlreadyExistsException;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Chuyển Category thành CategoryResponse
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setStatus(category.getStatus());

        return response;
    }

    // Gán dữ liệu từ request vào Category
    private void applyRequestToEntity(
            Category category,
            CategoryRequest request) {

        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }

    // Tạo Category mới
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {

            log.warn("Category already exists, name={}",
                    request.getName());

            throw new CategoryAlreadyExistsException(request.getName());
        }

        Category category = new Category();

        applyRequestToEntity(category, request);

        category.setStatus("ACTIVE");

        categoryRepository.save(category);

        log.info("Category created successfully, id={}",
                category.getId());

        return mapToResponse(category);
    }

    // Lấy danh sách tất cả Category
    public List<CategoryResponse> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();

        List<CategoryResponse> responses = new ArrayList<>();

        for (Category category : categories) {
            responses.add(mapToResponse(category));
        }

        log.info("Get all categories successfully");

        return responses;
    }

    // Lấy Category theo ID
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", id));

        log.info("Get category successfully, id={}", id);

        return mapToResponse(category);
    }

    // Cập nhật Category
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", id));

        if (categoryRepository.existsByNameAndIdNot(
                request.getName(), id)) {

            log.warn("Category name already exists, name={}",
                    request.getName());

            throw new CategoryAlreadyExistsException(request.getName());
        }

        applyRequestToEntity(category, request);

        categoryRepository.save(category);

        log.info("Category updated successfully, id={}", id);

        return mapToResponse(category);
    }

    // Xóa Category theo ID
    public void deleteCategory(Long id) {

        categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", id));

        categoryRepository.deleteById(id);

        log.info("Category deleted successfully, id={}", id);
    }
}