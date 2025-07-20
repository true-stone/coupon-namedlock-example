package org.example.namelock.repository;

import org.example.namelock.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    Optional<CouponStock> findByCouponCode(String couponCode);
}
