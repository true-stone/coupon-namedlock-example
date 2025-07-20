package org.example.namelock.repository;

import org.example.namelock.entity.Coupon;
import org.example.namelock.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query(countName = "회원아이디와 쿠폰 재고로 쿠폰이 있는지 확인")
    boolean existsByUserIdAndCouponStock(Long userId, CouponStock couponStock);
}
