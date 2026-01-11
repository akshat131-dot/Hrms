package com.hrms.tenant.service;

import com.hrms.common.tenant.TenantDataSource;
import com.hrms.tenant.entity.Tenant;
import com.hrms.tenant.entity.TenantSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class TenantDataSourceService {

    private final TenantDataSource tenantDataSource;

    private final Map<Object, Object> dataSources = new HashMap<>();

    public TenantDataSourceService(TenantDataSource tenantDataSource) {
        this.tenantDataSource = tenantDataSource;
    }

    public void registerTenantDataSource(Tenant tenant) {
        TenantSettings settings = tenant.getSettings();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(settings.getDbUrl());
        config.setUsername(settings.getDbUsername());
        config.setPassword(settings.getDbPassword());
        config.setMaximumPoolSize(5);
        config.setPoolName("TENANT-" + settings.getDbName());

        DataSource dataSource = new HikariDataSource(config);
        dataSources.put(tenant.getCode(), dataSource);
        tenantDataSource.setTargetDataSources(new HashMap<>(dataSources));
        tenantDataSource.afterPropertiesSet();
    }
}
