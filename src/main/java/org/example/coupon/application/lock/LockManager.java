package org.example.coupon.application.lock;

import org.springframework.dao.CannotAcquireLockException;

/**
 * 비즈니스 로직에서 사용하는 분산 락 인터페이스 (Port).
 * 구현체는 infrastructure 계층에서 제공.
 */
public interface LockManager {

    /**
     * 주어진 lock key에 대해 락을 획득한 후, 비즈니스 로직을 실행한다.
     *
     * @param key           락 키
     * @param businessLogic 실행할 로직
     * @throws CannotAcquireLockException 락 획득 실패 시
     */
    void runWithLock(String key, Runnable businessLogic);
}
