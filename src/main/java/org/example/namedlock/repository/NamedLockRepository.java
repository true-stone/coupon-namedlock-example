package org.example.namedlock.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NamedLockRepository {

    private final JdbcTemplate jdbcTemplate;

    public NamedLockRepository(@Qualifier("lockJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean getLock(String lockName, int timeoutSeconds) {
        String sql = "SELECT GET_LOCK(?, ?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, lockName, timeoutSeconds);
        return Boolean.TRUE.equals(result);
    }

    public void releaseLock(String lockName) {
        String sql = "SELECT RELEASE_LOCK(?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, lockName);
    }
}
