package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.RoleRequest;
import com.example.ordermanagementsystem.dto.response.RoleResponse;
import com.example.ordermanagementsystem.entity.Role;
import com.example.ordermanagementsystem.exception.DuplicateResourceException;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Entity -> Response
    private RoleResponse mapToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRole(role.getRole());
        return response;
    }

    // Create
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByRole(request.getRole())) {
            throw new DuplicateResourceException(
                    "Role '" + request.getRole() + "' đã tồn tại.");
        }
        Role role = new Role();
        role.setRole(request.getRole());
        roleRepository.save(role);
        return mapToResponse(role);
    }

    // Get All
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleResponse> responses = new ArrayList<>();
        for (Role role : roles) {
            responses.add(mapToResponse(role));
        }
        return responses;
    }

    // Get By id
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return mapToResponse(role);
    }

    // Update
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        if (!role.getRole().equals(request.getRole())
                && roleRepository.existsByRole(request.getRole())) {
            throw new DuplicateResourceException(
                    "Role '" + request.getRole() + "' đã tồn tại.");
        }

        role.setRole(request.getRole());
        roleRepository.save(role);
        return mapToResponse(role);
    }

    // Delete
    public void deleteRole(Long id) {
        roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        roleRepository.deleteById(id);
    }
}