package org.example.namedlock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponFacade couponFacade;

    public void downloadCoupon(Long userId, String couponCode) {
        log.info("유저 ({}) 쿠폰코드 '{}' 쿠폰 다운로드 요청", userId, couponCode);
        couponFacade.issue(userId, couponCode);
    }

}
