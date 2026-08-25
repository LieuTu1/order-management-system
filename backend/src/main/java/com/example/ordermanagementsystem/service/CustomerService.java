package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.CustomerRequest;
import com.example.ordermanagementsystem.dto.response.CustomerResponse;
import com.example.ordermanagementsystem.entity.Customer;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Chuyển Customer thành CustomerResponse
    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();

        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setAddress(customer.getAddress());
        response.setStatus(customer.getStatus());

        return response;
    }

    // Gán dữ liệu từ request vào Customer
    private void applyRequestToEntity(
            Customer customer,
            CustomerRequest request) {

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
    }

    // Tạo Customer mới
    public CustomerResponse createCustomer(CustomerRequest request) {

        Customer customer = new Customer();

        applyRequestToEntity(customer, request);

        customer.setStatus("ACTIVE");

        customerRepository.save(customer);

        log.info("Customer created successfully, id={}",
                customer.getId());

        return mapToResponse(customer);
    }

    // Lấy danh sách tất cả Customer
    public List<CustomerResponse> getAllCustomers() {

        List<Customer> customers = customerRepository.findAll();

        List<CustomerResponse> responses = new ArrayList<>();

        for (Customer customer : customers) {
            responses.add(mapToResponse(customer));
        }

        log.info("Get all customers successfully");

        return responses;
    }

    // Lấy Customer có phân trang
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {

        Page<Customer> customerPage = customerRepository.findAll(pageable);

        log.info("Get customers page successfully, page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return customerPage.map(this::mapToResponse);
    }

    // Lấy Customer theo ID
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", id));

        log.info("Get customer successfully, id={}", id);

        return mapToResponse(customer);
    }

    // Cập nhật Customer
    public CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", id));

        applyRequestToEntity(customer, request);

        customerRepository.save(customer);

        log.info("Customer updated successfully, id={}", id);

        return mapToResponse(customer);
    }

    // Xóa Customer theo ID
    public void deleteCustomer(Long id) {

        customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", id));

        customerRepository.deleteById(id);

        log.info("Customer deleted successfully, id={}", id);
    }
}