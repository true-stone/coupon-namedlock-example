package org.example.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;

@Entity
@Table(
    name = "coupon_stock",
    indexes = {
        @Index(name = "idx_coupon_code", columnList = "coupon_code")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class CouponStock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("쿠폰 코드")
    @Column(name = "coupon_code", nullable = false, unique = true)
    private String couponCode;

    @Comment("총 발급 수량")
    @Column(name = "total_issued_quantity", nullable = false)
    private Long totalIssuedQuantity;

    @Comment("잔여 수량")
    @Column(name = "remaining_quantity", nullable = false)
    private Long remainingQuantity;

    @Builder
    public CouponStock(String couponCode, Long totalIssuedQuantity) {
        this.couponCode = couponCode;
        this.totalIssuedQuantity = totalIssuedQuantity;
        this.remainingQuantity = totalIssuedQuantity;
    }

    public void decrease(Long quantity) {
        if (this.remainingQuantity - quantity < 0) {
            log.info("쿠폰 재고 부족: [현재 재고: {}], [요청 수량: {}], [쿠폰 코드: {}]", this.remainingQuantity, quantity, this.couponCode);
            throw new IllegalStateException("쿠폰 재고가 없습니다.");
        }
        this.remainingQuantity -= quantity;
    }
}
