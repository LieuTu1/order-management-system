package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.dto.request.RoleRequest;
import com.example.ordermanagementsystem.dto.response.ApiResponse;
import com.example.ordermanagementsystem.dto.response.RoleResponse;
import com.example.ordermanagementsystem.service.RoleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(RoleController.class);

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // 1. GET all
    // Lấy danh sách tất cả Role
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRoles() {

        List<RoleResponse> roleResponses =
                roleService.getAllRoles();

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Role retrieved successfully");
        response.setData(roleResponses);

        log.info("Get all roles successfully");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 2. GET by id
    // Lấy Role theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(
            @PathVariable Long id) {

        RoleResponse roleResponse =
                roleService.getRoleById(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setData(roleResponse);

        log.info("Get role successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 3. POST create
    // Tạo Role mới
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody RoleRequest request) {

        RoleResponse roleResponse =
                roleService.createRole(request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Role created successfully");
        response.setData(roleResponse);

        log.info("Role created successfully, id={}",
                roleResponse.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. PUT update
    // Cập nhật Role
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {

        RoleResponse roleResponse =
                roleService.updateRole(id, request);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Role updated successfully");
        response.setData(roleResponse);

        log.info("Role updated successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 5. DELETE
    // Xóa Role theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id) {

        roleService.deleteRole(id);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Role deleted successfully");
        response.setData(null);

        log.info("Role deleted successfully, id={}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}