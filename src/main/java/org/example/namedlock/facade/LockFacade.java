package org.example.namedlock.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.namedlock.repository.NamedLockRepository;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockFacade {

    private final NamedLockRepository lockRepository;

    @Transactional(transactionManager = "lockTransactionManager")
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
