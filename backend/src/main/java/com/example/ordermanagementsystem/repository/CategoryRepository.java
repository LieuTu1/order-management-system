package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    //Có Category nào cùng tên ngoại trừ chính id này không
    boolean existsByNameAndIdNot(String name, Long id);
}