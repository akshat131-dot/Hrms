package com.hrms.common.tenant;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TenantDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> tenantDataSources = new HashMap<>();

    public TenantDataSource(DataSource defaultDataSource) {

        super.setDefaultTargetDataSource(defaultDataSource);


        super.setTargetDataSources(tenantDataSources);

        super.afterPropertiesSet();
    }

    public void addTenant(String tenantId, DataSource dataSource) {
        tenantDataSources.put(tenantId, dataSource);
        super.setTargetDataSources(tenantDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }
}
