package com.example.ordermanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String email;

    private String address;

    @Column(nullable = false)
    private String status;
}