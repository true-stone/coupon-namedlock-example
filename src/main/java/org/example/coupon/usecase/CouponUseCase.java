package org.example.coupon.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.facade.NamedLockFacade;
import org.example.coupon.service.CouponIssueService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUseCase {

    private final NamedLockFacade namedLockFacade;
    private final CouponIssueService couponIssueService;

    public void issueWithNamedLock(Long userId, String couponCode) {
        log.info("유저 ({}) 쿠폰코드 '{}' 쿠폰 다운로드 요청", userId, couponCode);

        String lockName = "coupon_stock:code:" + couponCode;
        namedLockFacade.runWithLock(lockName, () -> couponIssueService.issue(userId, couponCode));
    }

}
