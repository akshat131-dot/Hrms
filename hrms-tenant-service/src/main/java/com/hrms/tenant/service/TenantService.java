package com.hrms.tenant.service;

import com.hrms.common.exception.BusinessException;
import com.hrms.tenant.dto.CreateTenantRequest;
import com.hrms.tenant.dto.TenantResponse;
import com.hrms.tenant.entity.Tenant;
import com.hrms.tenant.entity.TenantSettings;
import com.hrms.tenant.entity.TenantStatus;
import com.hrms.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantDataSourceService tenantDataSourceService;

    public TenantService(TenantRepository tenantRepository, TenantDataSourceService tenantDataSourceService) {
        this.tenantRepository = tenantRepository;
        this.tenantDataSourceService = tenantDataSourceService;
    }

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Tenant code already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setCode(request.getCode());
        tenant.setStatus(TenantStatus.ACTIVE);

        String dbName = "tenant_" + request.getCode().toLowerCase();
        String dbUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;MODE=MySQL";

        TenantSettings settings = new TenantSettings();
        settings.setDbName(dbName);
        settings.setDbUrl(dbUrl);
        settings.setDbUsername("sa");
        settings.setDbPassword("");

        tenant.setSettings(settings);

        Tenant saved = tenantRepository.save(tenant);

        tenantDataSourceService.registerTenantDataSource(saved);

        return new TenantResponse(saved.getId(), saved.getName(), saved.getCode(), saved.getStatus(), saved.getCreatedAt());
    }

    public TenantResponse getTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tenant not found"));
        return new TenantResponse(tenant.getId(), tenant.getName(), tenant.getCode(), tenant.getStatus(), tenant.getCreatedAt());
    }
}
