package org.example.namedlock.repository;

import jakarta.persistence.QueryHint;
import org.example.namedlock.entity.Coupon;
import org.example.namedlock.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @QueryHints(@QueryHint(name = "org.hibernate.comment", value = "쿠폰 존재 여부 확인"))
    boolean existsByUserIdAndCouponStock(Long userId, CouponStock couponStock);
}
