package org.example.coupon.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NamedLockFacadeTest {

    @Autowired
    private NamedLockFacade namedLockFacade;

    @Test
    @DisplayName("이미 다른 스레드가 락을 점유 중일 때 락 획득 실패 예외가 발생해야 한다.")
    void shouldFailWhenLockAlreadyHeld() throws InterruptedException {
        // given
        String lockKey = "coupon_stock:code:ABC";
        CountDownLatch readyLatch = new CountDownLatch(1);
        CountDownLatch releaseLatch = new CountDownLatch(1);

        Thread lockerThread = new Thread(() -> {
            namedLockFacade.runWithLock(lockKey, () -> {
                try {
                    readyLatch.countDown(); // 락 획득 신호
                    releaseLatch.await();   // 해제 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        lockerThread.start();
        readyLatch.await(); // 락이 먼저 잡히길 기다림

        // when & then
        Exception exception = assertThrows(CannotAcquireLockException.class, () -> {
            namedLockFacade.runWithLock(lockKey, () -> {
                // 이 블록은 실행되지 않아야 함
            });
        });

        System.out.printf("❗ 예외 메시지: %s%n", exception.getMessage());
        assertEquals("Lock 획득 실패", exception.getMessage());

        // 락을 점유한 스레드를 정리
        releaseLatch.countDown();
        lockerThread.join();
    }
}