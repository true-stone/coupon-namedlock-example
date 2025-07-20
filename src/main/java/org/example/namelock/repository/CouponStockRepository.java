package org.example.namelock.repository;

import jakarta.persistence.QueryHint;
import org.example.namelock.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    @QueryHints(@QueryHint(name = "org.hibernate.comment", value = "쿠폰 코드로 쿠폰 재고 조회"))
    Optional<CouponStock> findByCouponCode(String couponCode);
}
