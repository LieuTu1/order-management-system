package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}