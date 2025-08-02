package org.example.coupon.infrastructure.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class LockDataSourceConfiguration {

    @Bean("lockHikariConfig")
    @ConfigurationProperties("spring.datasource.lock.hikari")
    public HikariConfig lockHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "lockDataSource")
    public DataSource lockDataSource(@Qualifier("lockHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

//    @Bean(name = "lockTransactionManager")
//    public PlatformTransactionManager lockTransactionManager(@Qualifier("lockDataSource") DataSource lockDataSource) {
//        return new DataSourceTransactionManager(lockDataSource);
//    }

    @Bean(name = "lockTransactionManager")
    public PlatformTransactionManager lockTransactionManager(@Qualifier("lockDataSource") DataSource lockDataSource) {
        DataSourceTransactionManager realManager = new DataSourceTransactionManager(lockDataSource);

        return new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
                log.info("🔒 lockTransactionManager - getTransaction(): {}", definition.getName());
                return realManager.getTransaction(definition);
            }

            @Override
            public void commit(TransactionStatus status) throws TransactionException {
                log.info("🔒 lockTransactionManager - commit()");
                realManager.commit(status);
            }

            @Override
            public void rollback(TransactionStatus status) throws TransactionException {
                log.info("🔒 lockTransactionManager - rollback()");
                realManager.rollback(status);
            }
        };
    }
}
