package org.example.coupon.service;

import org.example.coupon.entity.CouponStock;
import org.example.coupon.repository.CouponRepository;
import org.example.coupon.repository.CouponStockRepository;
import org.example.coupon.usecase.CouponUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("네임드 락을 사용한 쿠폰 발행 단건/동시성 테스트")
class CouponIssueWithNamedLockTest {

    @Autowired
    private CouponUseCase couponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponStockRepository couponStockRepository;

    private final String COUPON_CODE = "ABC123";

    @BeforeEach
    void setUp() {
        System.out.println("BeforeEach 실행");
        couponRepository.deleteAll();
        couponStockRepository.deleteAll();
        couponStockRepository.saveAndFlush(new CouponStock(COUPON_CODE, 1000L));
    }

    @AfterEach
    void cleanUp() {
        System.out.println("AfterEach 실행");
        // couponRepository.deleteAll();
        // couponStockRepository.deleteAll();
    }

    @Test
    @DisplayName("1건의 발급 요청이 있을 때 재고는 정확히 1 감소해야 한다.")
    public void decreaseStock() {
        // given
        Long userId = 1L;

        // when
        couponUseCase.issueWithNamedLock(userId, COUPON_CODE);

        // then
        CouponStock couponStock = couponStockRepository.findByCouponCode(COUPON_CODE).orElseThrow();

        // 999 = 1000 - 1
        assertEquals(999L, couponStock.getRemainingQuantity());
    }

    @Test
    @DisplayName("1,000개 쿠폰을 두고 10,000명이 경쟁해도 재고 초과는 발생하지 않는다.")
    void concurrentCouponIssueWithNamedLock() throws InterruptedException {
        // given
        int totalStock = 1000;
        int userCount = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(userCount);

        // when
        for (long i = 0; i < userCount; i++) {
            long userId = i + 1;
            executor.submit(() -> {
                try {
                    couponUseCase.issueWithNamedLock(userId, COUPON_CODE);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 작업이 완료될 때까지 대기

        // then
        // ✅ 최종 쿠폰 발급된 쿠폰 개수 확인
        long issuedCount = couponRepository.count();
        long remaining = couponStockRepository.findByCouponCode(COUPON_CODE)
            .map(CouponStock::getRemainingQuantity)
            .orElse(-1L);

        System.out.println("발급된 쿠폰 수: " + issuedCount);
        System.out.println("남은 재고 수량: " + remaining);

        assertEquals(totalStock, issuedCount, "발급된 쿠폰 수량은 재고 수량과 같아야 합니다.");
        assertEquals(0L, remaining, "남은 재고는 0이어야 합니다.");
    }
}