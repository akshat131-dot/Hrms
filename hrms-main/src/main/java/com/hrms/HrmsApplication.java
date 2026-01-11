package com.hrms;

import com.hrms.common.tenant.TenantDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = "com.hrms")
@EntityScan(basePackages = "com.hrms")
@EnableJpaRepositories(basePackages = "com.hrms")
public class HrmsApplication {

    // 1. We inject the values from application.properties using @Value
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public static void main(String[] args) {
        SpringApplication.run(HrmsApplication.class, args);
    }

    @Bean
    @Primary
    public DataSource masterDataSource() {
        // 2. We use the variables instead of hardcoded strings
        return DataSourceBuilder.create()
                .url(dbUrl)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username(dbUsername)
                .password(dbPassword)
                .build();
    }

    @Bean
    public TenantDataSource tenantDataSource(DataSource masterDataSource) {
        return new TenantDataSource(masterDataSource);
    }
}