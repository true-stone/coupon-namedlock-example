package org.example.coupon.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.application.lock.LockManager;
import org.example.coupon.infrastructure.lock.MySqlNamedLockFacade;
import org.example.coupon.application.service.CouponIssueService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueCouponUseCase {

    private final LockManager lockManager;
    private final CouponIssueService couponIssueService;

    public void issueWithNamedLock(Long userId, String couponCode) {
        log.info("유저 ({}) 쿠폰코드 '{}' 쿠폰 다운로드 요청", userId, couponCode);

        String lockName = "coupon_stock:code:" + couponCode;
        lockManager.runWithLock(lockName, () -> couponIssueService.issue(userId, couponCode));
    }

}
