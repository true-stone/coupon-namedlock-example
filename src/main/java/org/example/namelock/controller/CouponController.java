package org.example.namelock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.namelock.dto.CouponIssueRequest;
import org.example.namelock.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/issue")
    public ResponseEntity<String> issueCoupon(
        @RequestBody @Valid CouponIssueRequest request) {
        couponService.downloadCoupon(request.userId(), request.couponCode());
        return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
    }
}
