package com.hrms.tenant.dto;

import com.hrms.tenant.entity.TenantStatus;

import java.time.LocalDateTime;

public class TenantResponse {

    private Long id;
    private String name;
    private String code;
    private TenantStatus status;
    private LocalDateTime createdAt;

    public TenantResponse() {}

    public TenantResponse(Long id, String name, String code, TenantStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
