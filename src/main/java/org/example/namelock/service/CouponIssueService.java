package org.example.namelock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.namelock.entity.Coupon;
import org.example.namelock.entity.CouponStock;
import org.example.namelock.repository.CouponRepository;
import org.example.namelock.repository.CouponStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponRepository couponRepository;
    private final CouponStockRepository couponStockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void issueCoupon(Long userId, String couponCode) {
        CouponStock stock = couponStockRepository.findByCouponCode(couponCode)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 중복 발급 체크
        if (couponRepository.existsByUserIdAndCouponStock(userId, stock)) {
            log.info("이미 쿠폰을 발급받았습니다.");
            throw new IllegalStateException("이미 쿠폰을 발급받았습니다.");
        }

        // 재고 감소
        stock.decrease(1L);
        couponStockRepository.saveAndFlush(stock);

        // 쿠폰 발급
        couponRepository.save(Coupon.builder()
            .couponStock(stock)
            .userId(userId).build());

        log.info("쿠폰 발급 성공");
    }
}
