package org.example.namedlock.service;

import org.example.namedlock.entity.CouponStock;
import org.example.namedlock.repository.CouponRepository;
import org.example.namedlock.repository.CouponStockRepository;
import org.example.namedlock.repository.NamedLockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CouponFacadeTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponIssueService couponIssueService;

    @Autowired
    private CouponStockRepository couponStockRepository;

    @Autowired
    private CouponRepository couponRepository;

    private final String COUPON_CODE = "ABC123";

    @BeforeEach
    public void setUp() {
        couponStockRepository.saveAndFlush(new CouponStock(COUPON_CODE, 100L));
    }

    @AfterEach
    public void tearDown() {
        couponRepository.deleteAll();
        couponStockRepository.deleteAllInBatch();
    }

    @Test
    void 동시에_100명이_쿠폰을_요청한다() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 병렬 스레드 개수
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executorService.submit(() -> {
                try {
                    couponFacade.issue(userId, COUPON_CODE);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 작업이 완료될 때까지 대기

        // ✅ 최종 쿠폰 발급된 쿠폰 개수 확인
        long issuedCount = couponRepository.count();
        long remainingStock = couponStockRepository.findByCouponCode(COUPON_CODE)
            .map(CouponStock::getRemainingQuantity)
            .orElse(-1L);

        System.out.println("발급된 쿠폰 수: " + issuedCount);
        System.out.println("남은 재고 수량: " + remainingStock);

        // 검증: 100개 이하만 발급되어야 함
        assertThat(issuedCount).isLessThanOrEqualTo(100);
        assertThat(remainingStock).isLessThanOrEqualTo(0);
        assertThat(100L - issuedCount).isEqualTo(0L);
    }

    @Test
    void 동시에_100명_이상_쿠폰_요청해도_재고_초과되지_않음() throws InterruptedException {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (long i = 1; i <= threadCount; i++) {
            final long userId = i;
            executorService.execute(() -> {
                try {
                    couponFacade.issue(userId, COUPON_CODE);
                } catch (Exception e) {
                    // 실패한 경우도 있을 수 있음 (예: 재고 부족, 중복 발급 등)
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long issuedCount = couponRepository.count();
        Long remainingStock = couponStockRepository.findByCouponCode(COUPON_CODE)
            .map(CouponStock::getRemainingQuantity)
            .orElse(-1L);

        System.out.println("발급된 쿠폰 수: " + issuedCount);
        System.out.println("남은 재고 수량: " + remainingStock);

        // ✅ 최종 쿠폰 발급 수는 최대 100개를 넘지 않아야 한다.
        assertThat(issuedCount).isLessThanOrEqualTo(100L);
        assertThat(remainingStock).isLessThanOrEqualTo(0L);
        assertThat(100L - issuedCount).isEqualTo(0L);
    }

    @Test
    void 락_획득에_실패하면_예외가_발생한다() {

        NamedLockRepository mockLockRepo = mock(NamedLockRepository.class);
        CouponFacade couponFacade = new CouponFacade(couponIssueService, mockLockRepo);

        when(mockLockRepo.getLock(anyString(), anyInt())).thenReturn(false);

        // expect
        assertThatThrownBy(() -> couponFacade.issue(1234L, "COUPON123"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("잠시 후 다시 시도해 주세요");

        // verify
        verify(mockLockRepo).getLock(eq("lock:coupon:COUPON123"), anyInt());
    }
}