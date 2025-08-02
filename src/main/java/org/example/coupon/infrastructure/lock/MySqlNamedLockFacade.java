package org.example.coupon.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.application.lock.LockManager;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySqlNamedLockFacade implements LockManager {

    private final NamedLockRepository lockRepository;

    @Transactional(transactionManager = "lockTransactionManager")
    @Override
    public void runWithLock(String key, Runnable businessLogic) {
        log.debug("🔐 Try lock: lockKey={}", key);

        try {
            boolean lock = lockRepository.getLock(key, 5);
            if (!lock) {
                log.warn("Failed to acquire lock: lockKey={}", key);
                throw new CannotAcquireLockException("Lock 획득 실패");
            }

            businessLogic.run();
        } finally {
            lockRepository.releaseLock(key);
        }
    }
}
