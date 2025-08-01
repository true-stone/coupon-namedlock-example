package org.example.namedlock.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfiguration {

    @Bean(name = "lockJdbcTemplate")
    public JdbcTemplate lockJdbcTemplate(@Qualifier("lockDataSource") DataSource lockDataSource) {
        return new JdbcTemplate(lockDataSource);
    }

    @Bean(name = "namedLockJdbcTemplate")
    public NamedParameterJdbcTemplate namedLockJdbcTemplate(@Qualifier("lockDataSource") DataSource lockDataSource) {
        return new NamedParameterJdbcTemplate(lockDataSource);
    }
}
