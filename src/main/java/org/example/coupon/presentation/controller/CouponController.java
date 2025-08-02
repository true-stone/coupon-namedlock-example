package org.example.coupon.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.coupon.presentation.request.CouponIssueRequest;
import org.example.coupon.application.usecase.IssueCouponUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final IssueCouponUseCase issueCouponUseCase;

    @PostMapping("/issue")
    public ResponseEntity<String> issue(
        @RequestBody @Valid CouponIssueRequest request) {
        issueCouponUseCase.issueWithNamedLock(request.userId(), request.couponCode());
        return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
    }
}
