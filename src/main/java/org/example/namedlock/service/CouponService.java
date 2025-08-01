package org.example.namedlock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.namedlock.facade.LockFacade;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final LockFacade lockFacade;
    private final CouponIssueService couponIssueService;

    public void issueWithNamedLock(Long userId, String couponCode) {
        log.info("유저 ({}) 쿠폰코드 '{}' 쿠폰 다운로드 요청", userId, couponCode);

        String lockName = "coupon_stock:code:" + couponCode;
        lockFacade.runWithLock(lockName, () -> couponIssueService.issue(userId, couponCode));
    }

}
