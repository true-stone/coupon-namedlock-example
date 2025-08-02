package org.example.coupon.domain.repository;

import jakarta.persistence.QueryHint;
import org.example.coupon.domain.entity.Coupon;
import org.example.coupon.domain.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @QueryHints(@QueryHint(name = "org.hibernate.comment", value = "쿠폰 존재 여부 확인"))
    boolean existsByUserIdAndCouponStock(Long userId, CouponStock couponStock);
}
