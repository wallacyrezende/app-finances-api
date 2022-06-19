package com.dev.finances.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final RemoteSecurityConfig remoteSecurityConfig;

    public DataSourceConfig(RemoteSecurityConfig remoteSecurityConfig){
        this.remoteSecurityConfig = remoteSecurityConfig;
    }

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(remoteSecurityConfig.getDriverClassName());
        dataSourceBuilder.url(remoteSecurityConfig.getUrl());
        dataSourceBuilder.username(remoteSecurityConfig.getUsername());
        dataSourceBuilder.password(remoteSecurityConfig.getPassword());
        return dataSourceBuilder.build();
    }
}
