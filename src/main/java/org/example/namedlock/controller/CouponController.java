package org.example.namedlock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.namedlock.dto.CouponIssueRequest;
import org.example.namedlock.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/issue")
    public ResponseEntity<String> issue(
        @RequestBody @Valid CouponIssueRequest request) {
        couponService.issueWithNamedLock(request.userId(), request.couponCode());
        return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
    }
}
