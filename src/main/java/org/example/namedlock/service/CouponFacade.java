package org.example.namedlock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.namedlock.repository.NamedLockRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponFacade {

    private static final int TIMEOUT_SECONDS = 3;

    private final CouponIssueService couponIssueService;
    private final NamedLockRepository namedLockRepository;

    @Transactional(transactionManager = "lockTransactionManager")
    public void issue(Long userId, String couponCode) {
        String lockName = "lock:coupon:" + couponCode;
        log.info("🔐 Try lock: lockKey={}", lockName);

        try {
            boolean lockAcquired = namedLockRepository.getLock(lockName, TIMEOUT_SECONDS);
            if (!lockAcquired) {
                log.warn("락 획득 실패: lockKey={} / 다른 사용자에 의해 사용 중", lockName);
                throw new IllegalStateException("잠시 후 다시 시도해 주세요. 현재 처리 중입니다.");
            }
            couponIssueService.issueCoupon(userId, couponCode);
        } finally {
            namedLockRepository.releaseLock(lockName);
        }
    }
}
