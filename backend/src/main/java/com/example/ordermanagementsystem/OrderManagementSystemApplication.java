package com.example.ordermanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OrderManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagementSystemApplication.class, args);
    }

}
