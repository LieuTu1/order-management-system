package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.SupplierRequest;
import com.example.ordermanagementsystem.dto.response.SupplierResponse;
import com.example.ordermanagementsystem.entity.Supplier;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // 1. Entity -> Response
    private SupplierResponse mapToResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setPhone(supplier.getPhone());
        response.setAddress(supplier.getAddress());
        response.setEmail(supplier.getEmail());
        return response;
    }

    // 2. Create
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setStatus("Active");
        supplier.setAddress(request.getAddress());
        supplierRepository.save(supplier);

        return mapToResponse(supplier);
    }

    // 3. Get All
    public List<SupplierResponse> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        List<SupplierResponse> responses = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            responses.add(mapToResponse(supplier));
        }
        return responses;
    }

    // 4. Get By id
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        return mapToResponse(supplier);
    }

    // 5. Update
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setStatus("Active");
        supplier.setAddress(request.getAddress());
        supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    // 6. Delete
    public void deleteSupplier(Long id) {
        supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        supplierRepository.deleteById(id);
    }
}