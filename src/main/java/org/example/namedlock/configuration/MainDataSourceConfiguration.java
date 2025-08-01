package org.example.namedlock.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MainDataSourceConfiguration {

    public static final String MAIN_DATA_SOURCE_PROPERTIES = "mainDataSourceProperties";
    public static final String MAIN_HIKARI_CONFIG = "mainHikariConfig";
    public static final String MAIN_DATA_SOURCE = "mainDataSource";

    @Primary
    @Bean(MAIN_DATA_SOURCE_PROPERTIES)
    @ConfigurationProperties("spring.datasource.main")
    public DataSourceProperties mainDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(MAIN_HIKARI_CONFIG)
    @ConfigurationProperties("spring.datasource.main.hikari")
    public HikariConfig mainHikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(MAIN_DATA_SOURCE)
    public DataSource dataSource(
        @Qualifier(MAIN_DATA_SOURCE_PROPERTIES) DataSourceProperties properties,
        @Qualifier(MAIN_HIKARI_CONFIG) HikariConfig hikariConfig
    ) {
        hikariConfig.setJdbcUrl(properties.getUrl());
        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setPassword(properties.getPassword());
        hikariConfig.setDriverClassName(properties.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }
}
