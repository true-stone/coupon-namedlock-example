package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.entity.Coupon;
import org.example.coupon.entity.CouponStock;
import org.example.coupon.repository.CouponRepository;
import org.example.coupon.repository.CouponStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponRepository couponRepository;
    private final CouponStockRepository couponStockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void issue(Long userId, String couponCode) {
        StopWatch stopWatch = new StopWatch("쿠폰 발급");

        stopWatch.start("1. 쿠폰 코드 존재여부 확인");
        CouponStock stock = couponStockRepository.findByCouponCode(couponCode)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
        stopWatch.stop();

        // 중복 발급 체크
        stopWatch.start("2. 쿠폰 중복 발급 여부 확인");
        if (couponRepository.existsByUserIdAndCouponStock(userId, stock)) {
            log.info("이미 쿠폰을 발급받았습니다.");
            throw new IllegalStateException("이미 쿠폰을 발급받았습니다.");
        }
        stopWatch.stop();

        // 재고 감소
        stopWatch.start("3. 쿠폰 재고 감소 처리");
        stock.decrease(1L);
        couponStockRepository.saveAndFlush(stock);
        stopWatch.stop();

        // 쿠폰 발급
        stopWatch.start("4. 쿠폰 발급 처리");
        couponRepository.save(Coupon.builder()
            .couponStock(stock)
            .userId(userId).build());
        stopWatch.stop();

        log.info("쿠폰 발급 성공 \n{}", stopWatch.prettyPrint());
    }
}
