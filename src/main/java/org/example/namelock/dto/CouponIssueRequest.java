package org.example.namelock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CouponIssueRequest(
    @NotNull Long userId,
    @NotBlank String couponCode
) {

}
