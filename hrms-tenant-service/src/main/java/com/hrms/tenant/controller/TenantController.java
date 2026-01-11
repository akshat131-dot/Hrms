package com.hrms.tenant.controller;

import com.hrms.common.response.ApiResponse;
import com.hrms.tenant.dto.CreateTenantRequest;
import com.hrms.tenant.dto.TenantResponse;
import com.hrms.tenant.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.ok(ApiResponse.success("Tenant created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenant(@PathVariable Long id) {
        TenantResponse response = tenantService.getTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant fetched successfully", response));
    }
}
